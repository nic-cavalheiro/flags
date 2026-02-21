// 1. IMPORTANTE: Adicione 'Input' na importação do angular/core
import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-wiki-1',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './wiki-1.component.html',
  styleUrls: ['./wiki-1.component.scss']
})
export class Wiki1Component {
  @Input() data: any; 
}