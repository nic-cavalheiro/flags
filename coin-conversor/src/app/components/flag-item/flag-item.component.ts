import {ChangeDetectionStrategy ,SimpleChanges ,Component, Input, Output, EventEmitter, ViewChild, ElementRef, OnInit, OnChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ModalComponent } from '../modal/modal.component'; // Altere o caminho conforme necessário

@Component({
  selector: 'app-flag-item',
  standalone: true,
  templateUrl: './flag-item.component.html',
  styleUrls: ['./flag-item.component.scss'],
  imports: [CommonModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FlagItemComponent implements OnInit, OnChanges {

  selectedFlag: { name: string; imageUrl: string } | null = null;

  @Input() countryName!: string;
  countryInfo: any;

  constructor(public elementRef: ElementRef) {}
  
  @ViewChild('nameFlag') nameFlag!: ElementRef;
  
  isModalOpen = false;
  @Input() isDarkTheme = false;
  @Input() flag!: { name: string; imageUrl: string };
  @Input() isExpanded = false;
  
  @Output() flagClicked = new EventEmitter<{name: string; imageUrl: string}>();

  onFlagClicked() {
    this.flagClicked.emit(this.flag);
  }

  onClick(event: MouseEvent, selectedFlagItem:any) {
    console.log('Método Chamado: onClick(), em FlagItem')
    console.log('Bandeira clicada: ', this.flag.name);

    this.isExpanded = !this.isExpanded;
    console.log('"isExpanded": utilizado em FlagItem, Modal aberto!')
    this.flagClicked.emit(this.flag);
    console.log('Evento flagClicked emitido para:', this.flag);
  }

  ngOnInit() {
    console.log('isDarkTheme recebido:', this.isDarkTheme);
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['isDarkTheme'] && !changes['isDarkTheme'].isFirstChange()) {
      const previousValue = changes['isDarkTheme'].previousValue;
      const currentValue = changes['isDarkTheme'].currentValue;
      if (previousValue !== currentValue) {
        console.log('isDarkTheme atualizado:', currentValue);
      }
    }
  }

  onThemeChanged(isDark: boolean) {
    this.isDarkTheme = isDark;
    console.log('Tema propagado para o ItemComponent:', this.isDarkTheme);
  }
}


