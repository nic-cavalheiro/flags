import { AfterViewInit, Component, TemplateRef, Input, Output } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { Location, CommonModule } from '@angular/common';
import { EventEmitter, ViewChild, ElementRef } from '@angular/core';
import { FlagItemComponent } from '../flag-item/flag-item.component';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [RouterModule, FlagItemComponent, CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent{
  
  @ViewChild('nameFlag') nameFlag!: ElementRef;

  goHome() {
    window.location.reload();
  }
  
 isDarkTheme = false;
   @Output() themeChanged = new EventEmitter<boolean>();


  toggleTheme() {
    console.log('Método toggleTheme chamado');
    
    this.isDarkTheme = !this.isDarkTheme;
    this.themeChanged.emit(this.isDarkTheme);
    document.body.classList.toggle('dark-theme', this.isDarkTheme)
    
    console.log('Tema alterado:', this.isDarkTheme);
    }
  }
