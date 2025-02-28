// import { Component, EventEmitter, Output, OnInit } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { FlagService } from '../../services/flags-api-service.service';  // Serviço correto

// @Component({
//   selector: 'app-header',
//   standalone: true,
//   imports: [CommonModule, FormsModule],
//   templateUrl: './header.component.html',
//   styleUrls: ['./header.component.scss']
// })
// export class HeaderComponent implements OnInit {
//   searchTerm: string = '';
//   filteredFlags: any[] = [];
//   isFlagsLoaded = false;  // Variável para verificar se as bandeiras foram carregadas
//   flags: any[] = [];

//   @Output() themeChanged = new EventEmitter<boolean>();
//   isDarkTheme = false;

//   constructor(private flagService: FlagService) {}  // Correção do nome do serviço

//   ngOnInit() {
//     // Consumir as bandeiras da API através do serviço
//     this.flagService.getFlags().subscribe((flags: any[]) => {  // Correção no uso do serviço
//       this.flags = flags;
//       this.filteredFlags = flags;  // Inicializa com todas as bandeiras
//       this.isFlagsLoaded = true;
//       console.log('Bandeiras carregadas:', flags);
//     }, (error) => {
//       console.error('Erro ao carregar as bandeiras:', error);
//     });
//   }

//   toggleTheme() {
//     this.isDarkTheme = !this.isDarkTheme;
//     this.themeChanged.emit(this.isDarkTheme);
//     document.body.classList.toggle('dark-theme', this.isDarkTheme);
//   }

//   goHome() {
//     window.location.reload();
//   }

//   onSearchBarClick() {
//     console.log('Barra de pesquisa clicada');
//   }

//   onFlagsLoaded(flags: any[]) {
//     this.flags = flags;  // Atualiza as bandeiras no HeaderComponent
//     this.isFlagsLoaded = true;
//     console.log('Bandeiras carregadas no HeaderComponent:', flags);
//   }

//   filterFlags() {
//     if (!this.isFlagsLoaded) {
//       console.warn('⚠️ As bandeiras ainda não foram carregadas.');
//       return;
//     }

//     console.log('🔍 Filtrando bandeiras com o termo:', this.searchTerm);

//     this.filteredFlags = this.searchTerm
//       ? this.flags.filter(flag => flag.name.toLowerCase().includes(this.searchTerm.toLowerCase()))
//       : this.flags;

//     console.log('🔍 Bandeiras filtradas:', this.filteredFlags);
//   }

//   selectFlag(flag: any) {
//     this.searchTerm = flag.name;
//     this.filteredFlags = [];
//   }
// }

import { Component, EventEmitter, Output, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { FlagService } from '../../services/flags-api-service.service';  // Serviço correto

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './header.component.html',
  styleUrls: ['./header.component.scss']
})
export class HeaderComponent implements OnInit {
  searchTerm: string = ''; // Termo de pesquisa
  filteredFlags: any[] = []; // Bandeiras filtradas
  isFlagsLoaded = false;  // Verifica se as bandeiras foram carregadas
  flags: any[] = []; // Todas as bandeiras carregadas
  selectedFlag: any = null;  // Bandeira selecionada para exibição no modal

  @Output() themeChanged = new EventEmitter<boolean>();
  isDarkTheme = false;

  constructor(private flagService: FlagService) {}  // Correção no nome do serviço

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
  
    this.filteredFlags = this.searchTerm
      ? this.flags.filter(flag => flag.name.toLowerCase().startsWith(this.searchTerm.toLowerCase()))
      : [];
  
    console.log('🔍 Bandeiras filtradas:', this.filteredFlags);
  }
  

  // Seleção da bandeira e abertura do modal
  selectFlag(flag: any) {
    this.selectedFlag = flag;  // Armazena a bandeira selecionada
    console.log('Bandeira selecionada:', flag);

    // Aqui você deve integrar com a lógica do modal
    this.openModal();
  }

  // Função para abrir o modal
  openModal() {
    // Lógica para abrir o modal com a bandeira selecionada
    console.log('Abrindo o modal para:', this.selectedFlag.name);
    
    // Aqui, você pode definir um valor ou chamar um método para abrir o modal
    // Exemplo:
    // this.modalService.openModal(this.selectedFlag);
  }
}










