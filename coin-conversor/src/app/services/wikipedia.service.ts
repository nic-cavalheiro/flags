import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class WikipediaService {

  private apiUrl = 'http://localhost:8080/api/wikipedia'; // Ajuste a porta conforme necessário

  constructor(private http: HttpClient) {}

  // Método para obter informações gerais do país
  getCountryInfo(country: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${encodeURIComponent(country)}`);
  }

  // Método para obter informações econômicas do país
  getEconomicInfo(country: string): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${encodeURIComponent(country)}/economics`);
  }
}