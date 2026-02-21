import { Component, Input, Output, EventEmitter, OnInit, OnDestroy, OnChanges, SimpleChanges } from '@angular/core';
import { NgIf } from '@angular/common';
import { CommonModule } from '@angular/common';
import { Wiki1Component } from './views/wiki-1/wiki-1.component';
import { Economy1Component } from './views/economy-1/economy-1.component';
import { Map1Component } from './views/map-1/map-1.component';
import { Arts1Component } from './views/arts-1/arts-1.component';
import { FlagListComponent } from '../flag-list/flag-list.component';

@Component({
  selector: 'app-modal',
  standalone: true,
  templateUrl: './modal.component.html',
  styleUrls: ['./modal.component.scss'],
  imports: [CommonModule,
    Wiki1Component,
    Economy1Component,
    Map1Component,
    Arts1Component,
    NgIf]
})

export class ModalComponent implements OnInit, OnDestroy, OnChanges {

  @Input() isModalOpen = false;
  @Input() selectedFlag: { name: string; imageUrl: string } | null = null;
  @Input() countryInfo: any;
  @Input() economicInfo: any;
  @Input() geographyInfo: any; // AI generated
  @Input() artInfo: any; // AI generated
  @Input() isDataLoaded: boolean = false;

  @Output() close = new EventEmitter<void>();

  activeView: string = 'mural';
  isChildFullscreen: boolean = false;

  currentArtIndex: number = 0;
  private carouselInterval: any;

  onFullscreenToggled(state: boolean) {
    this.isChildFullscreen = state;
  }

  // Atualize também a função que recebe o clique
  navigateTo(view: string) {
    console.log('Navegando para:', view);
    this.activeView = view;
  }

  openSubPage(view: 'wiki' | 'economy' | 'map' | 'arts') {
    console.log('Expandindo para:', view);
    this.activeView = view;
    // Força o scroll para o topo para a nova página
    const content = document.querySelector('.modal-content');
    if (content) content.scrollTo({ top: 0, behavior: 'smooth' });
  }

  backToMural() {
    this.activeView = 'mural';
  }

  ngOnInit() {
    document.body.classList.add('no-scroll');
  }

  ngOnDestroy() {
    this.removeNoScroll();
  }

  onClose() {
    console.log('Modal fechado');
    this.removeNoScroll(); // Garante a remoção antes do emit
    this.close.emit();
    console.log('Close Emitido!!');
  }

  private removeNoScroll() {
    document.body.classList.remove('no-scroll');
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['selectedFlag'] && this.selectedFlag) {
      this.activeView = 'mural';

      if (this.carouselInterval) {
        clearInterval(this.carouselInterval);
      }
      this.currentArtIndex = 0;

      if (this.artInfo?.artworks?.length > 0) {
        this.carouselInterval = setInterval(() => {
          if (this.artInfo && this.artInfo.artworks) {
            this.currentArtIndex = (this.currentArtIndex + 1) % this.artInfo.artworks.length;
          }
        }, 3500); // 3.5 seconds
      }
    } else if (changes['artInfo'] && this.artInfo?.artworks?.length > 0) {
      if (this.carouselInterval) {
        clearInterval(this.carouselInterval);
      }
      this.currentArtIndex = 0;
      this.carouselInterval = setInterval(() => {
        if (this.artInfo && this.artInfo.artworks) {
          this.currentArtIndex = (this.currentArtIndex + 1) % this.artInfo.artworks.length;
        }
      }, 3500);
    }
  }
}