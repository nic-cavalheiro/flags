import { Component, Input } from '@angular/core';
@Component({
  selector: 'app-arts-1',
  standalone: true,
  imports: [],
  templateUrl: './arts-1.component.html',
  styleUrl: './arts-1.component.scss'
})
export class Arts1Component {
  @Input() flagData: any; // O nome aqui deve ser igual ao do HTML [flagData]
}
