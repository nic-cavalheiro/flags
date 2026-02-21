import { Component, Input } from '@angular/core';

@Component({
  selector: 'app-map-1',
  standalone: true,
  imports: [],
  templateUrl: './map-1.component.html',
  styleUrl: './map-1.component.scss'
})
export class Map1Component {
  // AI generated variable to receive geography info
  @Input() data: any;
}
