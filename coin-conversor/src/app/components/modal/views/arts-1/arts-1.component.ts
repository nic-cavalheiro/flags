import { Component, Input, Output, EventEmitter, OnChanges, SimpleChanges } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Arts2Component } from './arts-2/arts-2.component';

export interface Artwork {
  title: string;
  author: string;
  date: string;
  style: string;
  historicalPeriod: string;
  city: string;
  imageUrl: string;
  computedCentury?: string;
  displayCategory?: string;
}

export interface ArtMetadata {
  country: string;
  artworks: Artwork[];
}

export interface ArtCluster {
  clusterName: string;
  earliestYear: number;
  works: Artwork[];
}

@Component({
  selector: 'app-arts-1',
  standalone: true,
  imports: [CommonModule, Arts2Component],
  templateUrl: './arts-1.component.html',
  styleUrl: './arts-1.component.scss'
})
export class Arts1Component implements OnChanges {
  @Input() flagData: any;
  @Input() data: ArtMetadata | undefined;

  groupedArtworks: ArtCluster[] = [];
  selectedArt: Artwork | null = null;
  selectedImage: string | null = null;

  @Output() fullscreenToggled = new EventEmitter<boolean>();

  openFullscreen(art: Artwork) {
    this.selectedArt = art;
    this.fullscreenToggled.emit(true);
  }

  openFullscreenImage(img: string) {
    this.selectedImage = img;
    this.fullscreenToggled.emit(true);
  }

  closeFullscreen() {
    this.selectedArt = null;
    this.selectedImage = null;
    this.fullscreenToggled.emit(false);
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['data'] && this.data?.artworks) {
      this.complexClustering(this.data.artworks);
    }
  }

  private complexClustering(artworks: Artwork[]) {
    const clustersMap = new Map<string, Artwork[]>();

    artworks.forEach(art => {
      let clusterName = 'Acervo Geral';
      let centuryStr = '';

      const hasPeriod = art.historicalPeriod && !art.historicalPeriod.includes('não especificado') && art.historicalPeriod !== 'null';
      const hasStyle = art.style && !art.style.includes('não especificado') && art.style !== 'null';
      const hasDate = art.date && art.date.trim() !== '';

      // 1. Calcula o Século
      if (hasDate) {
        const year = parseInt(art.date.replace(/\D/g, ''));
        if (!isNaN(year) && year > 0) {
          centuryStr = `Século ${this.toRoman(Math.ceil(year / 100))}`;
          art.computedCentury = centuryStr;
        }
      }

      // 2. Agrupa de forma mais ampla (Evita criar dezenas de grupos com 1 obra só)
      if (hasPeriod) {
        clusterName = art.historicalPeriod;
      } else if (centuryStr) {
        clusterName = `Obras do ${centuryStr}`;
      } else {
        clusterName = 'Registros Históricos';
      }

      // O estilo vai aparecer na Badge da imagem
      art.displayCategory = hasStyle ? art.style : (centuryStr || 'Obra Histórica');

      if (!clustersMap.has(clusterName)) {
        clustersMap.set(clusterName, []);
      }
      clustersMap.get(clusterName)!.push(art);
    });

    this.groupedArtworks = Array.from(clustersMap.entries()).map(([name, works]) => {
      works.sort((a, b) => this.extractYear(a.date) - this.extractYear(b.date));
      return {
        clusterName: name,
        earliestYear: works[0] ? this.extractYear(works[0].date) : 9999,
        works: works
      };
    }).sort((a, b) => {
      if (a.clusterName.includes('Registros')) return 1;
      if (b.clusterName.includes('Registros')) return -1;
      return a.earliestYear - b.earliestYear;
    });
  }

  private extractYear(dateStr: string): number {
    if (!dateStr) return 9999;
    const year = parseInt(dateStr.replace(/\D/g, ''));
    return isNaN(year) ? 9999 : year;
  }

  private toRoman(num: number): string {
    const romanNumerals: { [key: string]: number } = { M: 1000, CM: 900, D: 500, CD: 400, C: 100, XC: 90, L: 50, XL: 40, X: 10, IX: 9, V: 5, IV: 4, I: 1 };
    let result = '';
    for (let key in romanNumerals) {
      while (num >= romanNumerals[key]) {
        result += key;
        num -= romanNumerals[key];
      }
    }
    return result;
  }
}