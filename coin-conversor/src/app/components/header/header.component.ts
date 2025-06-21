import { Component, EventEmitter, Output, OnInit, ViewChild, AfterViewInit, ChangeDetectorRef, OnChanges, Input, SimpleChanges} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FlagService } from '../../services/flags-api/flags-api-service.service';
import { FlagListComponent } from '../flag-list/flag-list.component';
import { FlagItemComponent } from '../flag-item/flag-item.component';
import { ModalComponent } from '../modal/modal.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, FormsModule, ModalComponent, FlagItemComponent],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit, AfterViewInit, OnChanges {
  
  
  @ViewChild(FlagListComponent) flagList!: FlagListComponent;
  @ViewChild(FlagItemComponent) flagItem!: FlagItemComponent;  // Referência ao FlagItemComponent
  
  @Input() isExpanded = false;
  @Input() countryInfo: any = null;  // Receber dados do país
  @Input() economicInfo: any = null; // Receber dados econômicos
  @Input() isDataLoaded: boolean = false;  // Para saber se os dados estão carregados
 
  @Output() flagSelected = new EventEmitter<any>();
  
  @Output() themeChanged = new EventEmitter<boolean>();
  @Output() closeModalEvent = new EventEmitter<void>();

  searchTerm: string = ''; // Termo de pesquisa
  filteredFlags: any[] = []; // Bandeiras filtradas
  isFlagsLoaded = false;  // Verifica se as bandeiras foram carregadas
  flags: any[] = []; // Todas as bandeiras carregadas
  
  @Input() selectedFlag: { name: string; imageUrl: string } | null = { name: '', imageUrl: '' };

  
  isModalOpen = false;
  
  isDarkTheme = false;

  pendingFlag: any = null;  // 👈 nova variável para guardar clique pendente


  constructor(private flagService: FlagService , private cdr: ChangeDetectorRef) {}  // Correção no nome do serviço

  ngOnChanges(changes: SimpleChanges) {
    if (changes['isExpanded']) {
      console.log('isExpanded foi alterado:', changes['isExpanded'].currentValue);
    }
  }

  ngAfterViewInit() {
    this.cdr.detectChanges();
    console.log('FlagListComponent carregado:', this.flagList);
    console.log('FlagItemComponent carregado:', this.flagItem);

      // Se alguém já clicou em uma bandeira antes do flagList nascer
  if (this.pendingFlag) {
    console.log('⏳ Processando clique pendente:', this.pendingFlag);
    this.flagList.onFlagClicked(this.pendingFlag);
    this.pendingFlag = null;  // reseta
  }
}

  ngOnInit() {
    // Consumir as bandeiras da API através do serviço
    this.flagService.getFlags().subscribe((flags: any[]) => {
      this.flags = flags;
      this.filteredFlags = flags;  // Inicializa com todas as bandeiras
      this.isFlagsLoaded = true;
      console.log('Bandeiras carregadas:', flags);
    }, (error) => {
      console.error('Erro ao carregar as bandeiras:', error);
    });
  }

  toggleTheme() {
    this.isDarkTheme = !this.isDarkTheme;
    this.themeChanged.emit(this.isDarkTheme);
    document.body.classList.toggle('dark-theme', this.isDarkTheme);
  }

  goHome() {
    window.location.reload();
  }

  onSearchBarClick() {
    console.log('Barra de pesquisa clicada');
  }

  onFlagsLoaded(flags: any[]) {
    this.flags = flags;  // Atualiza as bandeiras no HeaderComponent
    this.isFlagsLoaded = true;
    console.log('Bandeiras carregadas no HeaderComponent:', flags);
  }

  // Método de filtragem
  filterFlags() {
    if (!this.isFlagsLoaded) {
      console.warn('⚠️ As bandeiras ainda não foram carregadas.');
      return;
    }
  
    console.log('🔍 Filtrando bandeiras com o termo:', this.searchTerm);
  
    this.filteredFlags = this.flags
      ? this.flags.filter(flag => flag.name.toLowerCase().startsWith(this.searchTerm.toLowerCase()))
      : [];
  
    console.log('🔍 Bandeiras filtradas:', this.filteredFlags);
  }
  
  selectFlag(flag: any) {
  this.flagSelected.emit(flag);
  this.selectedFlag = flag;
 
  console.log('Bandeira Emitida:', this.selectedFlag);
    // Verifique se selectedFlag não é null antes de passá-la para FlagListComponent
    if (this.selectedFlag) {
      console.log('selectedFlag' + this.selectedFlag + 'foi passado para o ' +
        'FlagListComponent');
    } else {
      console.error('⚠️ selectedFlag é null!');
    }
    return this.selectedFlag
  }

  onClick(event: MouseEvent, flag: any) {
    if (this.flagItem) {
      // Chama o método onClick diretamente no FlagItemComponent
      this.flagItem.onClick(event, flag);
    } else {
      console.error('FlagItemComponent ainda não foi carregado!');
    }
  }
  onFlagClicked(flag: any) {
    console.log('🏁 Flag clicada:', flag);
    this.flagSelected.emit(flag); // <- já está emitindo para os outros componentes
    this.selectedFlag = flag;
    /*this.isModalOpen = true;*/
    this.isExpanded = !this.isExpanded;
  }

}










