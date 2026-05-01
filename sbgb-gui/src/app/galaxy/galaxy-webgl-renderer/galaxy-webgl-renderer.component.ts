import {
  Component,
  ElementRef,
  HostListener,
  Input,
  OnChanges,
  OnDestroy,
  ViewChild,
  AfterViewInit
} from '@angular/core';
import { MatIconButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatTooltip } from '@angular/material/tooltip';
import { StarParticleDto } from '../galaxy.model';

// Temperature LUT from Galaxy-Renderer-Typescript/Helper.ts
// 200 entries, minTemp=1000K, maxTemp=10000K
const TEMPERATURE_LUT_RGB: number[] = [
  1,-0.00987248,-0.0166818, 1,0.000671682,-0.0173831, 1,0.0113477,-0.0179839,
  1,0.0221357,-0.0184684, 1,0.0330177,-0.0188214, 1,0.0439771,-0.0190283,
  1,0.0549989,-0.0190754, 1,0.0660696,-0.0189496, 1,0.0771766,-0.0186391,
  1,0.0883086,-0.0181329, 1,0.0994553,-0.017421, 1,0.110607,-0.0164945,
  1,0.121756,-0.0153455, 1,0.132894,-0.0139671, 1,0.144013,-0.0123534,
  1,0.155107,-0.0104993, 1,0.166171,-0.0084008, 1,0.177198,-0.00605465,
  1,0.188184,-0.00345843, 1,0.199125,-0.000610485, 1,0.210015,0.00249014,
  1,0.220853,0.00584373, 1,0.231633,0.00944995, 1,0.242353,0.0133079,
  1,0.25301,0.0174162, 1,0.263601,0.021773, 1,0.274125,0.0263759,
  1,0.284579,0.0312223, 1,0.294962,0.0363091, 1,0.305271,0.0416328,
  1,0.315505,0.0471899, 1,0.325662,0.0529765, 1,0.335742,0.0589884,
  1,0.345744,0.0652213, 1,0.355666,0.0716707, 1,0.365508,0.078332,
  1,0.375268,0.0852003, 1,0.384948,0.0922709, 1,0.394544,0.0995389,
  1,0.404059,0.106999, 1,0.41349,0.114646, 1,0.422838,0.122476,
  1,0.432103,0.130482, 1,0.441284,0.138661, 1,0.450381,0.147005,
  1,0.459395,0.155512, 1,0.468325,0.164175, 1,0.477172,0.172989,
  1,0.485935,0.181949, 1,0.494614,0.19105, 1,0.503211,0.200288,
  1,0.511724,0.209657, 1,0.520155,0.219152, 1,0.528504,0.228769,
  1,0.536771,0.238502, 1,0.544955,0.248347, 1,0.553059,0.2583,
  1,0.561082,0.268356, 1,0.569024,0.27851, 1,0.576886,0.288758,
  1,0.584668,0.299095, 1,0.592372,0.309518, 1,0.599996,0.320022,
  1,0.607543,0.330603, 1,0.615012,0.341257, 1,0.622403,0.35198,
  1,0.629719,0.362768, 1,0.636958,0.373617, 1,0.644122,0.384524,
  1,0.65121,0.395486, 1,0.658225,0.406497, 1,0.665166,0.417556,
  1,0.672034,0.428659, 1,0.678829,0.439802, 1,0.685552,0.450982,
  1,0.692204,0.462196, 1,0.698786,0.473441, 1,0.705297,0.484714,
  1,0.711739,0.496013, 1,0.718112,0.507333, 1,0.724417,0.518673,
  1,0.730654,0.53003, 1,0.736825,0.541402, 1,0.742929,0.552785,
  1,0.748968,0.564177, 1,0.754942,0.575576, 1,0.760851,0.586979,
  1,0.766696,0.598385, 1,0.772479,0.609791, 1,0.778199,0.621195,
  1,0.783858,0.632595, 1,0.789455,0.643989, 1,0.794991,0.655375,
  1,0.800468,0.666751, 1,0.805886,0.678116, 1,0.811245,0.689467,
  1,0.816546,0.700803, 1,0.82179,0.712122, 1,0.826976,0.723423,
  1,0.832107,0.734704, 1,0.837183,0.745964, 1,0.842203,0.757201,
  1,0.847169,0.768414, 1,0.852082,0.779601, 1,0.856941,0.790762,
  1,0.861748,0.801895, 1,0.866503,0.812999, 1,0.871207,0.824073,
  1,0.87586,0.835115, 1,0.880463,0.846125, 1,0.885017,0.857102,
  1,0.889521,0.868044, 1,0.893977,0.878951, 1,0.898386,0.889822,
  1,0.902747,0.900657, 1,0.907061,0.911453, 1,0.91133,0.922211,
  1,0.915552,0.932929, 1,0.91973,0.943608, 1,0.923863,0.954246,
  1,0.927952,0.964842, 1,0.931998,0.975397, 1,0.936001,0.985909,
  1,0.939961,0.996379,
  0.993241,0.9375,1, 0.983104,0.931743,1, 0.973213,0.926103,1,
  0.963562,0.920576,1, 0.954141,0.915159,1, 0.944943,0.909849,1,
  0.935961,0.904643,1, 0.927189,0.899538,1, 0.918618,0.894531,1,
  0.910244,0.88962,1, 0.902059,0.884801,1, 0.894058,0.880074,1,
  0.886236,0.875434,1, 0.878586,0.87088,1, 0.871103,0.86641,1,
  0.863783,0.862021,1, 0.856621,0.857712,1, 0.849611,0.853479,1,
  0.84275,0.849322,1, 0.836033,0.845239,1, 0.829456,0.841227,1,
  0.823014,0.837285,1, 0.816705,0.83341,1, 0.810524,0.829602,1,
  0.804468,0.825859,1, 0.798532,0.82218,1, 0.792715,0.818562,1,
  0.787012,0.815004,1, 0.781421,0.811505,1, 0.775939,0.808063,1,
  0.770561,0.804678,1, 0.765287,0.801348,1, 0.760112,0.798071,1,
  0.755035,0.794846,1, 0.750053,0.791672,1, 0.745164,0.788549,1,
  0.740364,0.785474,1, 0.735652,0.782448,1, 0.731026,0.779468,1,
  0.726482,0.776534,1, 0.722021,0.773644,1, 0.717638,0.770798,1,
  0.713333,0.767996,1, 0.709103,0.765235,1, 0.704947,0.762515,1,
  0.700862,0.759835,1, 0.696848,0.757195,1, 0.692902,0.754593,1,
  0.689023,0.752029,1, 0.685208,0.749502,1, 0.681458,0.747011,1,
  0.67777,0.744555,1, 0.674143,0.742134,1, 0.670574,0.739747,1,
  0.667064,0.737394,1, 0.663611,0.735073,1, 0.660213,0.732785,1,
  0.656869,0.730528,1, 0.653579,0.728301,1, 0.65034,0.726105,1,
  0.647151,0.723939,1, 0.644013,0.721801,1, 0.640922,0.719692,1,
  0.637879,0.717611,1, 0.634883,0.715558,1, 0.631932,0.713531,1,
  0.629025,0.711531,1, 0.626162,0.709557,1, 0.623342,0.707609,1,
  0.620563,0.705685,1, 0.617825,0.703786,1, 0.615127,0.701911,1,
  0.612469,0.70006,1, 0.609848,0.698231,1, 0.607266,0.696426,1,
  0.60472,0.694643,1
];

const LUT_SIZE = 200;

const VERTEX_SHADER = `
  precision mediump float;

  attribute float a_semiMajor;
  attribute float a_semiMinor;
  attribute float a_theta;
  attribute float a_tiltAngle;
  attribute float a_temperature;
  attribute float a_magnitude;
  attribute float a_type;

  uniform float u_galaxyRadius;
  uniform float u_time;
  uniform float u_dustSize;
  uniform float u_pertAmp;
  uniform int u_pertN;
  uniform sampler2D u_colorLut;

  varying vec4 v_color;
  varying float v_type;

  vec3 colorFromTemperature(float temp) {
    float minTemp = 1000.0;
    float maxTemp = 10000.0;
    float t = clamp((temp - minTemp) / (maxTemp - minTemp), 0.0, 1.0);
    float u = (t * float(${LUT_SIZE} - 1) + 0.5) / float(${LUT_SIZE});
    vec4 c = texture2D(u_colorLut, vec2(u, 0.5));
    return max(c.rgb, vec3(0.0));
  }

  void main() {
    float theta = a_theta + u_time * 0.0001;
    float cosT = cos(a_tiltAngle);
    float sinT = sin(a_tiltAngle);
    float cosTheta = cos(theta);
    float sinTheta = sin(theta);
    float rx = a_semiMajor * cosTheta * cosT - a_semiMinor * sinTheta * sinT;
    float ry = a_semiMajor * cosTheta * sinT + a_semiMinor * sinTheta * cosT;

    if (u_pertAmp > 0.0 && u_pertN > 0) {
      rx += (a_semiMajor / u_pertAmp) * sin(theta * 2.0 * float(u_pertN));
      ry += (a_semiMajor / u_pertAmp) * cos(theta * 2.0 * float(u_pertN));
    }

    gl_Position = vec4(rx / u_galaxyRadius, ry / u_galaxyRadius, 0.0, 1.0);

    vec3 col = colorFromTemperature(a_temperature);

    if (a_type < 0.5) {
      // STAR (type 0)
      gl_PointSize = a_magnitude * 4.0;
      v_color = vec4(col * a_magnitude, 1.0);
    } else if (a_type < 1.5) {
      // DUST (type 1)
      gl_PointSize = a_magnitude * 5.0 * u_dustSize;
      v_color = vec4(col * a_magnitude, 1.0);
    } else if (a_type < 2.5) {
      // FILAMENT (type 2)
      gl_PointSize = a_magnitude * 2.0 * u_dustSize;
      v_color = vec4(col * a_magnitude, 1.0);
    } else if (a_type < 3.5) {
      // H2_OUTER (type 3) — rougeâtre
      gl_PointSize = a_magnitude * 2.0 * u_dustSize;
      v_color = vec4(col * a_magnitude * vec3(2.0, 0.5, 0.5), 1.0);
    } else {
      // H2_CORE (type 4) — blanc pur
      gl_PointSize = a_magnitude * u_dustSize * 0.5;
      v_color = vec4(1.0, 1.0, 1.0, 1.0);
    }

    v_type = a_type;
  }
`;

const FRAGMENT_SHADER = `
  precision mediump float;

  varying vec4 v_color;
  varying float v_type;

  void main() {
    vec2 circCoord = 2.0 * gl_PointCoord - 1.0;
    float dist = length(circCoord);
    float alpha;

    if (v_type < 0.5) {
      // STAR (type 0)
      alpha = 1.0 - dist;
      if (alpha < 0.01) discard;
      gl_FragColor = vec4(v_color.rgb, alpha);
    } else if (v_type < 1.5) {
      // DUST (type 1)
      alpha = 0.05 * (1.0 - dist);
      if (alpha < 0.003) discard;
      gl_FragColor = vec4(v_color.rgb, alpha);
    } else if (v_type < 2.5) {
      // FILAMENT (type 2)
      alpha = 0.07 * (1.0 - dist);
      if (alpha < 0.003) discard;
      gl_FragColor = vec4(v_color.rgb, alpha);
    } else {
      // H2_OUTER / H2_CORE (type 3 & 4)
      alpha = 1.0 - dist;
      if (alpha < 0.01) discard;
      gl_FragColor = vec4(v_color.rgb, alpha);
    }
  }
`;

@Component({
  selector: 'app-galaxy-webgl-renderer',
  standalone: true,
  imports: [MatIconButton, MatIcon, MatTooltip],
  template: `
    <div #container class="webgl-wrapper">
      <div class="canvas-scroll" [class.is-real-size]="isRealSize">
        <canvas #glCanvas
          [width]="size"
          [height]="size"
          style="display:block; background:#000;">
        </canvas>
      </div>
      <div class="image-controls">
        <button mat-icon-button
                (click)="toggleRealSize()"
                [matTooltip]="isRealSize ? 'Ajuster à la fenêtre' : 'Taille réelle (1:1)'">
          <mat-icon>{{ isRealSize ? 'close_fullscreen' : 'open_in_full' }}</mat-icon>
        </button>
        <button mat-icon-button
                (click)="toggleFullscreen()"
                [matTooltip]="isFullscreen ? 'Quitter le plein écran' : 'Plein écran'">
          <mat-icon>{{ isFullscreen ? 'fullscreen_exit' : 'fullscreen' }}</mat-icon>
        </button>
      </div>
    </div>
  `,
  styles: [`
    :host { display: block; width: 100%; }
    .webgl-wrapper {
      position: relative;
      background: #1a1a1a;
      border-radius: 8px;
      border: 1px solid #333;
    }
    .canvas-scroll {
      overflow: hidden;
      display: flex;
      align-items: center;
      justify-content: center;
    }
    .canvas-scroll canvas {
      max-width: 100%;
      max-height: calc(100vh - 280px);
      width: auto;
      height: auto;
    }
    .canvas-scroll.is-real-size {
      overflow: auto;
      align-items: flex-start;
      justify-content: flex-start;
    }
    .canvas-scroll.is-real-size canvas {
      max-width: none;
      max-height: none;
      width: auto;
      height: auto;
    }
    .webgl-wrapper:fullscreen {
      width: 100vw;
      height: 100vh;
      border-radius: 0;
      border: none;
    }
    .webgl-wrapper:fullscreen .canvas-scroll {
      width: 100%;
      height: 100%;
    }
    .webgl-wrapper:fullscreen .canvas-scroll canvas {
      max-width: 100vw;
      max-height: 100vh;
    }
    .webgl-wrapper:fullscreen .canvas-scroll.is-real-size canvas {
      max-width: none;
      max-height: none;
    }
    .image-controls {
      position: absolute;
      top: 8px;
      right: 8px;
      z-index: 10;
      display: flex;
      gap: 4px;
    }
    .image-controls button {
      background: rgba(0,0,0,0.5);
    }
    .image-controls button:hover {
      background: rgba(0,0,0,0.75);
    }
    .image-controls mat-icon {
      color: white;
    }
  `]
})
export class GalaxyWebglRendererComponent implements AfterViewInit, OnChanges, OnDestroy {

  @ViewChild('glCanvas') canvasRef!: ElementRef<HTMLCanvasElement>;
  @ViewChild('container') containerRef!: ElementRef<HTMLDivElement>;

  isRealSize = false;
  isFullscreen = false;

  @HostListener('document:fullscreenchange')
  onFullscreenChange(): void {
    this.isFullscreen = !!document.fullscreenElement;
  }

  toggleRealSize(): void {
    this.isRealSize = !this.isRealSize;
  }

  toggleFullscreen(): void {
    if (this.isFullscreen) {
      document.exitFullscreen();
    } else {
      this.containerRef?.nativeElement.requestFullscreen();
    }
  }

  @Input() particles: StarParticleDto[] = [];
  @Input() galaxyRadius: number = 15000;
  @Input() dustSize: number = 70;
  @Input() pertN: number = 0;
  @Input() pertAmp: number = 0;
  @Input() size: number = 800;

  private gl: WebGLRenderingContext | null = null;
  private program: WebGLProgram | null = null;
  private lutTexture: WebGLTexture | null = null;
  private buffers: { [key: string]: WebGLBuffer } = {};
  private particleCount: number = 0;

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
    // no animation loop to cancel
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
    const vs = this.compileShader(gl, gl.VERTEX_SHADER, VERTEX_SHADER);
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

    this.lutTexture = this.createLutTexture(gl);

    gl.enable(gl.BLEND);
    gl.blendFunc(gl.SRC_ALPHA, gl.ONE);
    gl.clearColor(0, 0, 0, 1);
  }

  private createLutTexture(gl: WebGLRenderingContext): WebGLTexture {
    const tex = gl.createTexture()!;
    gl.bindTexture(gl.TEXTURE_2D, tex);

    const data = new Uint8Array(LUT_SIZE * 3);
    for (let i = 0; i < LUT_SIZE; i++) {
      data[i * 3 + 0] = Math.min(255, Math.max(0, Math.round(TEMPERATURE_LUT_RGB[i * 3 + 0] * 255)));
      data[i * 3 + 1] = Math.min(255, Math.max(0, Math.round(TEMPERATURE_LUT_RGB[i * 3 + 1] * 255)));
      data[i * 3 + 2] = Math.min(255, Math.max(0, Math.round(TEMPERATURE_LUT_RGB[i * 3 + 2] * 255)));
    }

    gl.texImage2D(gl.TEXTURE_2D, 0, gl.RGB, LUT_SIZE, 1, 0, gl.RGB, gl.UNSIGNED_BYTE, data);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MIN_FILTER, gl.LINEAR);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_MAG_FILTER, gl.LINEAR);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_S, gl.CLAMP_TO_EDGE);
    gl.texParameteri(gl.TEXTURE_2D, gl.TEXTURE_WRAP_T, gl.CLAMP_TO_EDGE);

    return tex;
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
      types[i]        = this.typeToFloat(p.type);
    }

    this.uploadBuffer('a_semiMajor', semiMajors);
    this.uploadBuffer('a_semiMinor', semiMinors);
    this.uploadBuffer('a_theta', thetas);
    this.uploadBuffer('a_tiltAngle', tiltAngles);
    this.uploadBuffer('a_temperature', temperatures);
    this.uploadBuffer('a_magnitude', magnitudes);
    this.uploadBuffer('a_type', types);

    this.particleCount = count;
    this.drawFrame();
  }

  private uploadBuffer(name: string, data: Float32Array): void {
    const gl = this.gl!;
    if (this.buffers[name]) gl.deleteBuffer(this.buffers[name]);
    const buf = gl.createBuffer()!;
    gl.bindBuffer(gl.ARRAY_BUFFER, buf);
    gl.bufferData(gl.ARRAY_BUFFER, data, gl.STATIC_DRAW);
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

  private typeToFloat(type: string): number {
    switch (type) {
      case 'STAR':     return 0.0;
      case 'DUST':     return 1.0;
      case 'H2_OUTER': return 3.0;
      case 'H2_CORE':  return 4.0;
      default:         return 2.0; // FILAMENT
    }
  }

  private drawFrame(): void {
    const gl = this.gl!;
    const program = this.program!;

    gl.useProgram(program);
    gl.viewport(0, 0, this.size, this.size);
    gl.clear(gl.COLOR_BUFFER_BIT);

    gl.uniform1f(gl.getUniformLocation(program, 'u_galaxyRadius'), this.galaxyRadius);
    gl.uniform1f(gl.getUniformLocation(program, 'u_time'), 0.0);
    gl.uniform1f(gl.getUniformLocation(program, 'u_dustSize'), this.dustSize);
    gl.uniform1f(gl.getUniformLocation(program, 'u_pertAmp'), this.pertAmp);
    gl.uniform1i(gl.getUniformLocation(program, 'u_pertN'), this.pertN);

    gl.activeTexture(gl.TEXTURE0);
    gl.bindTexture(gl.TEXTURE_2D, this.lutTexture);
    gl.uniform1i(gl.getUniformLocation(program, 'u_colorLut'), 0);

    ['a_semiMajor','a_semiMinor','a_theta','a_tiltAngle','a_temperature','a_magnitude','a_type']
      .forEach(name => this.bindBuffer(name));

    gl.drawArrays(gl.POINTS, 0, this.particleCount);
  }
}
