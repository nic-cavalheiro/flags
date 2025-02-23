import { Component, Input, Output } from '@angular/core';
import { FlagListComponent } from './components/flag-list/flag-list.component';
import { CommonModule } from '@angular/common';
import { HeaderComponent } from './components/header/header.component'; 
import { FooterComponent } from './components/footer/footer.component';
import { FlagItemComponent } from './components/flag-item/flag-item.component';
@Component({
  selector: 'app-roott',
  standalone: true,
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  imports: [FlagItemComponent, FlagListComponent, CommonModule, HeaderComponent, FooterComponent],
})
export class AppComponent {
  @Input() isDarkTheme = false;

  toggleExpand(flag: any): void {
    flag.expanded = !flag.expanded;
  }
  
  onThemeChanged(isDark: boolean) {
    this.isDarkTheme = isDark;
    console.log('Tema propagado para o AppComponent:', this.isDarkTheme);

  }
}
