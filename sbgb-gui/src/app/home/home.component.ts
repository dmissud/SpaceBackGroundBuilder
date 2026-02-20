import { Component } from '@angular/core';
import { MatCard, MatCardHeader, MatCardContent, MatCardActions } from '@angular/material/card';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { RouterLink } from '@angular/router';

@Component({
    selector: 'app-home',
    imports: [
      MatCard,
      MatCardHeader,
      MatCardContent,
      MatCardActions,
      MatButton,
      MatIcon,
      RouterLink
    ],
    templateUrl: './home.component.html',
    styleUrl: './home.component.scss'
})
export class HomeComponent {

}
