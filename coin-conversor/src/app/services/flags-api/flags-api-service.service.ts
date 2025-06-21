import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Flag } from '../../models/flag-model';  // Importe a interface Flag

@Injectable({
  providedIn: 'root'  // O serviço é registrado no root, tornando-o acessível em toda a aplicação
})
export class FlagService {

  private apiFlagsUrl = 'http://localhost:8080/api/flags';  // Exemplo de URL para buscar bandeiras

  constructor(private http: HttpClient) {}

  // Método para pegar bandeiras da API
  getFlags(): Observable<Flag[]> {
    return this.http.get<Flag[]>(this.apiFlagsUrl);
  }
}
