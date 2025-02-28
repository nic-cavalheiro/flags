// import { SimpleChanges, Component, OnInit, Input } from '@angular/core';
// import { FlagItemComponent } from '../flag-item/flag-item.component';
// import { CommonModule } from '@angular/common';
// import { ModalComponent } from '../modal/modal.component';
// import { FlagService } from '@services/flags-api-service.service';
// import { WikipediaService } from '@services/wikipedia.service'; // Adicione o serviço da Wikipédia
// import { EventEmitter} from '@angular/core';
// import { Output} from '@angular/core';

// @Component({
//   selector: 'app-flag-list',
//   standalone: true,
//   templateUrl: './flag-list.component.html',
//   styleUrls: ['./flag-list.component.scss'],
//   imports: [CommonModule, FlagItemComponent, ModalComponent,],
// })
// export class FlagListComponent implements OnInit {
//   flags: any[] = [];
//   @Output() flagsLoaded: EventEmitter<any[]> = new EventEmitter();
//   errorMessage: string | null = null; 
  
//   selectedFlag: { name: string; imageUrl: string } | null = null;
//   isModalOpen = false;
//   countryInfo: any = null; // Armazena os dados da Wikipédia
//   economicInfo: any = null;
//   isDataLoaded = false;

//   constructor(
//     private flagService: FlagService,
//     private wikipediaService: WikipediaService // Inicie o serviço da Wikipédia
//   ) {}

//   @Input() isDarkTheme = false;

//   ngOnInit() {
//     this.fetchFlags();
//   }

//   onFlagClicked(flag: { name: string; imageUrl: string }) {
//     if (this.selectedFlag && this.selectedFlag.name === flag.name) {
//       this.closeModal();
//     } else {
//       this.selectedFlag = flag;
//       this.isModalOpen = true;

//       // Resetar os dados antes de buscar
//       this.countryInfo = null;
//       this.economicInfo = null;  
//       this.isDataLoaded = false; 


//       this.wikipediaService.getCountryInfo(flag.name).subscribe(data => {
//         this.countryInfo = data;
//         console.log('Resumo da Wikipédia:', this.countryInfo);
//         this.checkDataLoaded();
//       });


//       this.wikipediaService.getEconomicInfo(flag.name).subscribe(economicData => {
//         this.economicInfo = economicData;
//         console.log('Informações econômicas:', this.economicInfo);
//         this.checkDataLoaded();
//       });
//     }
//   }

//   checkDataLoaded() {
//     if (this.countryInfo && this.economicInfo) {
//       this.isDataLoaded = true;
//       console.log('checkDataLoaded chamado')
//     }
//   }

//   onModalClose() {
//     console.log('Modal fechado');
//     this.selectedFlag = null;
//     this.isModalOpen = false;
//   }

//   closeModal() {
//     this.onModalClose(); // Chama o método onModalClose
//   }

//   fetchFlags() {
//     console.log('Iniciando o carregamento das bandeiras');
//     this.flagService.getFlags().subscribe(
//       (data) => {
//         console.log('Bandeiras carregadas:', data);
//         this.flags = data;
//         this.flagsLoaded.emit(this.flags);
//       },
//       (error) => {
//         console.error('Erro ao carregar as bandeiras:', error);
//         this.errorMessage = 'Erro ao carregar as bandeiras. Tente novamente mais tarde.';
//       }
//     );
//   }

//   expandedFlagIndex: number | null = null;

//   toggleFlag(index: number) {
//     this.expandedFlagIndex = this.expandedFlagIndex === index ? null : index;
//   }

//   ngOnChanges(changes: SimpleChanges) {
//     if (changes['isDarkTheme']) {
//       console.log('isDarkTheme atualizado:', this.isDarkTheme); 
//     }
//   }
// }

import { SimpleChanges, Component, OnInit, Input } from '@angular/core';
import { FlagItemComponent } from '../flag-item/flag-item.component';
import { CommonModule } from '@angular/common';
import { ModalComponent } from '../modal/modal.component';
import { FlagService } from '@services/flags-api-service.service';
import { WikipediaService } from '@services/wikipedia.service';
import { EventEmitter, Output } from '@angular/core';

@Component({
  selector: 'app-flag-list',
  standalone: true,
  templateUrl: './flag-list.component.html',
  styleUrls: ['./flag-list.component.scss'],
  imports: [CommonModule, FlagItemComponent, ModalComponent],
})
export class FlagListComponent implements OnInit {
  flags: any[] = [];
  @Output() flagsLoaded: EventEmitter<any[]> = new EventEmitter();
  errorMessage: string | null = null;
  
  // Variáveis relacionadas ao Modal
  selectedFlag: { name: string; imageUrl: string } | null = null;
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
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['isDarkTheme']) {
      console.log('isDarkTheme atualizado:', this.isDarkTheme);
    }
  }
}

