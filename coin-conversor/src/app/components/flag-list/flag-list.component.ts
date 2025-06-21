import { SimpleChanges, Component, OnInit, Input, OnChanges, ViewChild, ViewChildren, QueryList } from '@angular/core';
import { FlagItemComponent } from '../flag-item/flag-item.component';
import { CommonModule } from '@angular/common';
import { ModalComponent } from '../modal/modal.component';
import { FlagService } from '../../services/flags-api/flags-api-service.service';
import { WikipediaService } from '../../services/wikipedia-api/wikipedia.service';
import { EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-flag-list',
  standalone: true,
  templateUrl: './flag-list.component.html',
  styleUrls: ['./flag-list.component.scss'],
  imports: [CommonModule, FlagItemComponent, ModalComponent],
})
export class FlagListComponent implements OnInit, OnChanges {

  @ViewChildren(FlagItemComponent) flagItems!: QueryList<FlagItemComponent>;
  
  @Output() closeModalEvent = new EventEmitter<void>();

  @Input() isExpanded = false;

  @Input() flags: any[] = [];

  @Output() flagsLoaded: EventEmitter<any[]> = new EventEmitter();
  errorMessage: string | null = null;
  
  // Variáveis relacionadas ao Modal
  @Input() selectedFlag: { name: string; imageUrl: string } | null = null;
  
  isModalOpen = false;
  countryInfo: any = null;
  economicInfo: any = null;
  isDataLoaded = false;

  // Variáveis para pesquisa e filtragem
  searchQuery: string = ''; // Termo de pesquisa
  filteredFlags: any[] = []; // Bandeiras filtradas

  constructor(
    private flagService: FlagService,
    private wikipediaService: WikipediaService
  ) {}

  @Input() isDarkTheme = false;

  ngOnInit() {
    this.fetchFlags();
  }


  onFlagClicked(flag: { name: string; imageUrl: string }) {
    if (this.selectedFlag && this.selectedFlag.name === flag.name) {
      this.closeModal();

      this.isExpanded = !this.isExpanded;

      console.log('"isExpanded": utilizado em FlagList!')
      console.log('Modal Abertoo!!');
    } else {
      this.selectedFlag = flag;
      this.isModalOpen = true;

      // Resetar os dados antes de buscar
      this.countryInfo = null;
      this.economicInfo = null;
      this.isDataLoaded = false;

      // Buscar as informações da Wikipédia
      this.wikipediaService.getCountryInfo(flag.name).subscribe(data => {
        this.countryInfo = data;
        this.checkDataLoaded();
      });

      // Buscar informações econômicas
      this.wikipediaService.getEconomicInfo(flag.name).subscribe(economicData => {
        this.economicInfo = economicData;
        this.checkDataLoaded();
      });
    }
  }

  checkDataLoaded() {
    if (this.countryInfo && this.economicInfo) {
      this.isDataLoaded = true;
    }
  }

  onModalClose() {
    this.selectedFlag = null;
    this.isModalOpen = false;
    this.closeModalEvent.emit();
  }

  closeModal() {
    this.onModalClose();
  }

  fetchFlags() {
    this.flagService.getFlags().subscribe(
      (data) => {
        this.flags = data;
        this.filteredFlags = this.flags; // Inicialmente, todas as bandeiras são exibidas
        this.flagsLoaded.emit(this.flags);
      },
      (error) => {
        this.errorMessage = 'Erro ao carregar as bandeiras. Tente novamente mais tarde.';
      }
    );
  }

  // Método para filtrar as bandeiras com base na pesquisa
  filterFlags() {
    this.filteredFlags = this.flags.filter(flag => {
      return flag.name.toLowerCase().includes(this.searchQuery.toLowerCase());
    });
  }

  expandedFlagIndex: number | null = null;

  toggleFlag(index: number) {
    this.expandedFlagIndex = this.expandedFlagIndex === index ? null : index;
    console.log('Método "toggleFlag()" chamado em flagList!')
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['isDarkTheme']) {
      console.log('isDarkTheme atualizado:', this.isDarkTheme);
    }
  
    if (changes['selectedFlag'] && changes['selectedFlag'].currentValue) {
      console.log('Bandeira selecionada na barra:', this.selectedFlag);
  
      if (this.selectedFlag !== null) {
        setTimeout(() => {
          const selectedFlagItem = this.flagItems.find(item =>
            item.flag.name === this.selectedFlag!.name
          );
  
          if (selectedFlagItem) {
            const event: MouseEvent = new MouseEvent('click');
            selectedFlagItem.onClick(event, selectedFlagItem);
            console.log('onClick chamado! Em FlagItem para:', selectedFlagItem.flag.name);
          } else {
            console.log('Nenhum FlagItem encontrado para a bandeira selecionada.');
          }
        }, 0); // Espera o Angular terminar de desenhar
      }
    }
  }
}


