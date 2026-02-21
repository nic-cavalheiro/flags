import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-economy-1',
  standalone: true,
  imports: [CommonModule], // Importante para usar *ngIf e Pipes
  templateUrl: './economy-1.component.html',
  styleUrls: ['./economy-1.component.scss']
})
export class Economy1Component {
  @Input() data: any; // Aqui chegam os dados do economicInfo
  }