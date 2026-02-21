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
    selector: 'app-arts-2',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './arts-2.component.html',
    styleUrl: './arts-2.component.scss'
})
export class Arts2Component {
    @Input() selectedArt: Artwork | null = null;
    @Input() selectedImage: string | null = null;
    @Output() close = new EventEmitter<void>();

    onClose() {
        this.close.emit();
    }
}
