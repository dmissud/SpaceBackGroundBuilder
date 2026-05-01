import {
  Component,
  ElementRef,
  Input,
  OnChanges,
  OnDestroy,
  ViewChild,
  AfterViewInit
} from '@angular/core';
import { StarParticleDto } from '../galaxy.model';

const VERTEX_SHADER = `
  attribute float a_semiMajor;
  attribute float a_semiMinor;
  attribute float a_theta;
  attribute float a_tiltAngle;
  attribute float a_temperature;
  attribute float a_magnitude;
  attribute float a_type;

  uniform float u_galaxyRadius;
  uniform float u_time;

  varying vec3 v_color;
  varying float v_alpha;
  varying float v_type;

  vec3 temperatureToColor(float temp) {
    float t = clamp(temp / 40000.0, 0.0, 1.0);
    if (temp < 3500.0) {
      return mix(vec3(1.0, 0.2, 0.0), vec3(1.0, 0.5, 0.2), temp / 3500.0);
    } else if (temp < 6000.0) {
      return mix(vec3(1.0, 0.6, 0.3), vec3(1.0, 1.0, 0.9), (temp - 3500.0) / 2500.0);
    } else if (temp < 10000.0) {
      return mix(vec3(1.0, 1.0, 0.9), vec3(0.8, 0.9, 1.0), (temp - 6000.0) / 4000.0);
    } else {
      return mix(vec3(0.8, 0.9, 1.0), vec3(0.5, 0.7, 1.0), min((temp - 10000.0) / 20000.0, 1.0));
    }
  }

  void main() {
    float theta = a_theta + u_time * a_semiMajor * 0.000001;
    float cosT = cos(tiltAngle_rad(a_tiltAngle));
    float sinT = sin(tiltAngle_rad(a_tiltAngle));
    float x = a_semiMajor * cos(theta);
    float y = a_semiMinor * sin(theta);
    float rx = x * cos(a_tiltAngle) - y * sin(a_tiltAngle);
    float ry = x * sin(a_tiltAngle) + y * cos(a_tiltAngle);

    gl_Position = vec4(rx / u_galaxyRadius, ry / u_galaxyRadius, 0.0, 1.0);

    float baseSize = (a_type < 0.5) ? 2.5 : 1.5;
    gl_PointSize = baseSize * (0.5 + a_magnitude);

    v_color = temperatureToColor(a_temperature);
    v_alpha = (a_type < 0.5) ? 0.9 * a_magnitude : 0.3 * a_magnitude;
    v_type = a_type;
  }
`;

const VERTEX_SHADER_FIXED = `
  attribute float a_semiMajor;
  attribute float a_semiMinor;
  attribute float a_theta;
  attribute float a_tiltAngle;
  attribute float a_temperature;
  attribute float a_magnitude;
  attribute float a_type;

  uniform float u_galaxyRadius;
  uniform float u_time;

  varying vec3 v_color;
  varying float v_alpha;

  vec3 temperatureToColor(float temp) {
    if (temp < 3500.0) {
      return mix(vec3(1.0, 0.2, 0.0), vec3(1.0, 0.5, 0.2), temp / 3500.0);
    } else if (temp < 6000.0) {
      return mix(vec3(1.0, 0.6, 0.3), vec3(1.0, 1.0, 0.9), (temp - 3500.0) / 2500.0);
    } else if (temp < 10000.0) {
      return mix(vec3(1.0, 1.0, 0.9), vec3(0.8, 0.9, 1.0), (temp - 6000.0) / 4000.0);
    } else {
      return mix(vec3(0.8, 0.9, 1.0), vec3(0.5, 0.7, 1.0), min((temp - 10000.0) / 20000.0, 1.0));
    }
  }

  void main() {
    float theta = a_theta + u_time * 0.0001;
    float rx = a_semiMajor * cos(theta) * cos(a_tiltAngle) - a_semiMinor * sin(theta) * sin(a_tiltAngle);
    float ry = a_semiMajor * cos(theta) * sin(a_tiltAngle) + a_semiMinor * sin(theta) * cos(a_tiltAngle);

    gl_Position = vec4(rx / u_galaxyRadius, ry / u_galaxyRadius, 0.0, 1.0);

    float baseSize = (a_type < 0.5) ? 2.5 : 1.5;
    gl_PointSize = baseSize * (0.5 + a_magnitude);

    v_color = temperatureToColor(a_temperature);
    v_alpha = (a_type < 0.5) ? 0.9 * a_magnitude : 0.3 * a_magnitude;
  }
`;

const FRAGMENT_SHADER = `
  precision mediump float;

  varying vec3 v_color;
  varying float v_alpha;

  void main() {
    vec2 coord = gl_PointCoord - vec2(0.5);
    float dist = length(coord);
    float alpha = v_alpha * (1.0 - smoothstep(0.2, 0.5, dist));
    if (alpha < 0.01) discard;
    gl_FragColor = vec4(v_color, alpha);
  }
`;

@Component({
  selector: 'app-galaxy-webgl-renderer',
  standalone: true,
  template: `
    <canvas #glCanvas
      [width]="size"
      [height]="size"
      style="display:block; width:100%; height:100%; background:#000;">
    </canvas>
  `,
  styles: [`:host { display: block; width: 100%; height: 100%; }`]
})
export class GalaxyWebglRendererComponent implements AfterViewInit, OnChanges, OnDestroy {

  @ViewChild('glCanvas') canvasRef!: ElementRef<HTMLCanvasElement>;

  @Input() particles: StarParticleDto[] = [];
  @Input() galaxyRadius: number = 15000;
  @Input() size: number = 800;

  private gl: WebGLRenderingContext | null = null;
  private program: WebGLProgram | null = null;
  private animFrameId: number | null = null;
  private time: number = 0;
  private buffers: { [key: string]: WebGLBuffer } = {};

  ngAfterViewInit(): void {
    this.initWebGL();
    if (this.particles.length > 0) {
      this.uploadAndRender();
    }
  }

  ngOnChanges(): void {
    if (this.gl && this.program) {
      this.uploadAndRender();
    }
  }

  ngOnDestroy(): void {
    if (this.animFrameId !== null) {
      cancelAnimationFrame(this.animFrameId);
    }
  }

  private initWebGL(): void {
    const canvas = this.canvasRef.nativeElement;
    try {
      this.gl = (canvas.getContext('webgl', { antialias: true, alpha: false })
              || canvas.getContext('experimental-webgl', { antialias: true, alpha: false })) as WebGLRenderingContext | null;
    } catch {
      this.gl = null;
    }
    if (!this.gl) {
      console.warn('WebGL not available in this environment');
      return;
    }

    const gl = this.gl;
    const vs = this.compileShader(gl, gl.VERTEX_SHADER, VERTEX_SHADER_FIXED);
    const fs = this.compileShader(gl, gl.FRAGMENT_SHADER, FRAGMENT_SHADER);
    if (!vs || !fs) return;

    this.program = gl.createProgram()!;
    gl.attachShader(this.program, vs);
    gl.attachShader(this.program, fs);
    gl.linkProgram(this.program);

    if (!gl.getProgramParameter(this.program, gl.LINK_STATUS)) {
      console.error('WebGL program link error:', gl.getProgramInfoLog(this.program));
      return;
    }

    gl.enable(gl.BLEND);
    gl.blendFunc(gl.SRC_ALPHA, gl.ONE);
    gl.clearColor(0, 0, 0, 1);
  }

  private compileShader(gl: WebGLRenderingContext, type: number, source: string): WebGLShader | null {
    const shader = gl.createShader(type)!;
    gl.shaderSource(shader, source);
    gl.compileShader(shader);
    if (!gl.getShaderParameter(shader, gl.COMPILE_STATUS)) {
      console.error('Shader compile error:', gl.getShaderInfoLog(shader));
      return null;
    }
    return shader;
  }

  private uploadAndRender(): void {
    const gl = this.gl;
    const program = this.program;
    if (!gl || !program || this.particles.length === 0) return;

    const count = this.particles.length;
    const semiMajors   = new Float32Array(count);
    const semiMinors   = new Float32Array(count);
    const thetas       = new Float32Array(count);
    const tiltAngles   = new Float32Array(count);
    const temperatures = new Float32Array(count);
    const magnitudes   = new Float32Array(count);
    const types        = new Float32Array(count);

    for (let i = 0; i < count; i++) {
      const p = this.particles[i];
      semiMajors[i]   = p.semiMajorAxis;
      semiMinors[i]   = p.semiMinorAxis;
      thetas[i]       = p.theta0;
      tiltAngles[i]   = p.tiltAngle;
      temperatures[i] = p.temperature;
      magnitudes[i]   = Math.min(p.magnitude, 1.0);
      types[i]        = (p.type === 'STAR') ? 0.0 : 1.0;
    }

    this.createBuffer('a_semiMajor', semiMajors);
    this.createBuffer('a_semiMinor', semiMinors);
    this.createBuffer('a_theta', thetas);
    this.createBuffer('a_tiltAngle', tiltAngles);
    this.createBuffer('a_temperature', temperatures);
    this.createBuffer('a_magnitude', magnitudes);
    this.createBuffer('a_type', types);

    if (this.animFrameId !== null) cancelAnimationFrame(this.animFrameId);
    this.drawFrame(count);
  }

  private createBuffer(name: string, data: Float32Array): void {
    const gl = this.gl!;
    const program = this.program!;
    if (this.buffers[name]) gl.deleteBuffer(this.buffers[name]);
    const buf = gl.createBuffer()!;
    gl.bindBuffer(gl.ARRAY_BUFFER, buf);
    gl.bufferData(gl.ARRAY_BUFFER, data, gl.STATIC_DRAW);
    const loc = gl.getAttribLocation(program, name);
    if (loc >= 0) {
      gl.enableVertexAttribArray(loc);
      gl.vertexAttribPointer(loc, 1, gl.FLOAT, false, 0, 0);
    }
    this.buffers[name] = buf;
  }

  private bindBuffer(name: string): void {
    const gl = this.gl!;
    const program = this.program!;
    const buf = this.buffers[name];
    if (!buf) return;
    gl.bindBuffer(gl.ARRAY_BUFFER, buf);
    const loc = gl.getAttribLocation(program, name);
    if (loc >= 0) {
      gl.enableVertexAttribArray(loc);
      gl.vertexAttribPointer(loc, 1, gl.FLOAT, false, 0, 0);
    }
  }

  private drawFrame(count: number): void {
    const gl = this.gl!;
    const program = this.program!;

    gl.useProgram(program);
    gl.viewport(0, 0, this.size, this.size);
    gl.clear(gl.COLOR_BUFFER_BIT);

    const radiusLoc = gl.getUniformLocation(program, 'u_galaxyRadius');
    const timeLoc   = gl.getUniformLocation(program, 'u_time');
    gl.uniform1f(radiusLoc, this.galaxyRadius);
    gl.uniform1f(timeLoc, this.time);

    ['a_semiMajor','a_semiMinor','a_theta','a_tiltAngle','a_temperature','a_magnitude','a_type']
      .forEach(name => this.bindBuffer(name));

    gl.drawArrays(gl.POINTS, 0, count);
  }
}
