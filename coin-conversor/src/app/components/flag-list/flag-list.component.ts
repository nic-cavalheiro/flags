import { SimpleChanges, Component, OnInit, Input } from '@angular/core';
import { FlagItemComponent } from '../flag-item/flag-item.component';
import { CommonModule } from '@angular/common';
import { ModalComponent } from '../modal/modal.component';
import { FlagService } from '@services/flags-api-service.service';
import { WikipediaService } from '@services/wikipedia.service'; // Adicione o serviço da Wikipédia

@Component({
  selector: 'app-flag-list',
  standalone: true,
  templateUrl: './flag-list.component.html',
  styleUrls: ['./flag-list.component.scss'],
  imports: [CommonModule, FlagItemComponent, ModalComponent],
})
export class FlagListComponent implements OnInit {
  flags: any[] = [];
  errorMessage: string | null = null; 
  
  selectedFlag: { name: string; imageUrl: string } | null = null;
  isModalOpen = false;
  countryInfo: any = null; // Armazena os dados da Wikipédia

  constructor(
    private flagService: FlagService,
    private wikipediaService: WikipediaService // Inicie o serviço da Wikipédia
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
      
      // Buscar informações da Wikipédia do país
      this.wikipediaService.getCountryInfo(flag.name).subscribe(data => {
        this.countryInfo = data;  // Armazena os dados da Wikipédia
        console.log('Dados da Wikipédia:', this.countryInfo);
      });
    }
  }

  onModalClose() {
    console.log('Modal fechado');
    this.selectedFlag = null;
    this.isModalOpen = false;
  }

  closeModal() {
    this.onModalClose(); // Chama o método onModalClose
  }

  fetchFlags() {
    this.flagService.getFlags().subscribe(
      (data) => {
        this.flags = data;
        console.log('Bandeiras carregadas:', this.flags);
      },
      (error) => {
        console.error('Erro ao carregar as bandeiras:', error);
        this.errorMessage = 'Erro ao carregar as bandeiras. Tente novamente mais tarde.';
      }
    );
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

// import { SimpleChanges, Component, OnInit, Input } from '@angular/core';
// import { FlagItemComponent } from '../flag-item/flag-item.component';
// import { CommonModule } from '@angular/common';
// import { ModalComponent } from '../modal/modal.component'; // Altere o caminho conforme necessário  // Adicione isso
// import { FlagService } from '@services/flags-api-service.service';

// @Component({
//   selector: 'app-flag-list',
//   standalone: true,
//   templateUrl: './flag-list.component.html',
//   styleUrls: ['./flag-list.component.scss'],
//   imports: [CommonModule, FlagItemComponent, ModalComponent],
// })
// export class FlagListComponent implements OnInit {
//   flags: any[] = [];
//   errorMessage: string | null = null; 
  
//   constructor(private flagService: FlagService) {}

//   @Input() isDarkTheme = false;
  
//   selectedFlag: { name: string; imageUrl: string } | null = null;
//   isModalOpen = false;

//   ngOnInit() {
//     console.log('fetchFlags chamado');
//     this.fetchFlags();
//   }

//   onFlagClicked(flag: { name: string; imageUrl: string }) {
//   if (this.selectedFlag && this.selectedFlag.name === flag.name) {
//     this.closeModal();
//   } else {
//     this.selectedFlag = flag;
//     this.isModalOpen = true;
//     console.log('OnFlagClicked chamado, modal aberto?:', this.isModalOpen);
//   }
// }

//   closeModal() {
//     this.selectedFlag = null;
//     this.isModalOpen = false;
//     console.log('Modal fechado, estado:', this.isModalOpen);
//   }

//   fetchFlags() {
//     this.flagService.getFlags().subscribe(
//       (data) => {
//         this.flags = data;  // Atribui os dados das bandeiras à variável 'flags'
//         console.log('Bandeiras carregadas:', this.flags);
//       },
//       (error) => {
//         console.error('Erro ao carregar as bandeiras:', error);
//         this.errorMessage = 'Erro ao carregar as bandeiras. Tente novamente mais tarde.'; // Mensagem de erro
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
