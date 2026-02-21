import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';

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

@Component({
    selector: 'app-art-detail',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './art-detail.component.html',
    styleUrl: './art-detail.component.scss'
})
export class ArtDetailComponent {
    @Input() selectedArt: Artwork | null = null;
    @Input() selectedImage: string | null = null;
    @Output() close = new EventEmitter<void>();

    onClose() {
        this.close.emit();
    }
}
