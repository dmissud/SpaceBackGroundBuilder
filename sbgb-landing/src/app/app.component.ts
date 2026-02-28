import { Component } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';

interface Environment {
  name: string;
  label: string;
  description: string;
  url: string;
  badge: string;
  badgeColor: string;
  icon: string;
}

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [MatCardModule, MatButtonModule, MatChipsModule, MatIconModule],
  template: `
    <div class="landing-container">
      <header class="landing-header">
        <mat-icon class="galaxy-icon">blur_on</mat-icon>
        <h1>Space Background Builder</h1>
        <p class="subtitle">Générateur d'images de fond spatial</p>
      </header>

      <div class="environments-grid">
        @for (env of environments; track env.name) {
          <mat-card class="env-card">
            <mat-card-header>
              <mat-icon mat-card-avatar [style.color]="env.badgeColor">{{ env.icon }}</mat-icon>
              <mat-card-title>{{ env.label }}</mat-card-title>
              <mat-card-subtitle>
                <span class="badge" [style.background-color]="env.badgeColor">{{ env.badge }}</span>
              </mat-card-subtitle>
            </mat-card-header>
            <mat-card-content>
              <p>{{ env.description }}</p>
            </mat-card-content>
            <mat-card-actions>
              <a mat-raised-button [href]="env.url" [style.background-color]="env.badgeColor">
                <mat-icon>rocket_launch</mat-icon>
                Accéder
              </a>
            </mat-card-actions>
          </mat-card>
        }
      </div>
    </div>
  `,
  styles: [`
    .landing-container {
      min-height: 100vh;
      display: flex;
      flex-direction: column;
      align-items: center;
      justify-content: center;
      padding: 2rem;
      gap: 3rem;
    }

    .landing-header {
      text-align: center;
      display: flex;
      flex-direction: column;
      align-items: center;
      gap: 0.5rem;
    }

    .galaxy-icon {
      font-size: 4rem;
      width: 4rem;
      height: 4rem;
      color: #9c27b0;
      filter: drop-shadow(0 0 12px #9c27b0);
    }

    h1 {
      font-size: 2.5rem;
      font-weight: 300;
      color: #e8d5ff;
      text-shadow: 0 0 20px rgba(156, 39, 176, 0.5);
    }

    .subtitle {
      color: #9e9e9e;
      font-size: 1.1rem;
    }

    .environments-grid {
      display: flex;
      gap: 2rem;
      flex-wrap: wrap;
      justify-content: center;
    }

    .env-card {
      width: 300px;
      background: rgba(255, 255, 255, 0.05);
      border: 1px solid rgba(255, 255, 255, 0.1);
      backdrop-filter: blur(10px);
      color: #e0e0e0;
      transition: transform 0.2s, box-shadow 0.2s;
    }

    .env-card:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
    }

    .badge {
      padding: 2px 10px;
      border-radius: 12px;
      font-size: 0.75rem;
      font-weight: 500;
      color: white;
    }

    mat-card-content p {
      color: #bdbdbd;
      margin-top: 1rem;
      line-height: 1.6;
    }

    mat-card-actions {
      padding: 1rem;
    }

    a[mat-raised-button] {
      color: white;
      text-decoration: none;
      display: inline-flex;
      align-items: center;
      gap: 0.5rem;
    }
  `]
})
export class AppComponent {
  environments: Environment[] = [
    {
      name: 'dev',
      label: 'Développement',
      description: 'Environnement de développement. Contient les dernières fonctionnalités en cours de développement.',
      url: '/sbgb/dev/',
      badge: 'DEV',
      badgeColor: '#1976d2',
      icon: 'science'
    },
    {
      name: 'prod',
      label: 'Production',
      description: 'Environnement de production. Version stable et validée de l\'application.',
      url: '/sbgb/prod/',
      badge: 'PROD',
      badgeColor: '#388e3c',
      icon: 'verified'
    }
  ];
}
