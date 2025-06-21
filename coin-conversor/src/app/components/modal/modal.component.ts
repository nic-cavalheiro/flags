import { Component, Input, Output, EventEmitter } from '@angular/core';
import { NgIf } from '@angular/common';
import { FlagListComponent } from '../flag-list/flag-list.component';
@Component({
  selector: 'app-modal',
  standalone: true,
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.scss'],
  imports: [NgIf, ]
})

export class ModalComponent {

  @Input() isModalOpen = false;

  @Input() selectedFlag: { name: string; imageUrl: string } | null = null;
  @Input() countryInfo: any;
  @Input() economicInfo: any;
  @Input() isDataLoaded: boolean = false;

  @Output() close = new EventEmitter<void>();

  onClose() {
    console.log('Modal fechado');
    this.close.emit();
    console.log('Close Emitido!!')
  }
}