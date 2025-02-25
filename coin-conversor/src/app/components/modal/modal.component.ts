// import { Component, Input, Output, EventEmitter } from '@angular/core';
// import { CommonModule } from '@angular/common';

// @Component({
//   selector: 'app-modal',
//   standalone: true,
//   templateUrl: './modal.component.html',
//   styleUrls: ['./modal.component.scss'],
//   imports: [CommonModule],
// })
// export class ModalComponent {
//   @Input() title = 'Modal';
//   @Output() closed = new EventEmitter<void>();

//   closeModal() {
//     this.closed.emit();
//   }
// }
import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-modal',
  standalone: true,  // Defina standalone como true
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.scss'],
  imports: [CommonModule],  // Adicione CommonModule aos imports para acesso a funcionalidades comuns
})
export class ModalComponent {

  @Output() close = new EventEmitter<void>();

  onClose() {
    console.log('Modal fechado'); 
    this.close.emit();
  }
}