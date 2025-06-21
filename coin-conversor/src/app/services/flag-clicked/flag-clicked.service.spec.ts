import { TestBed } from '@angular/core/testing';

import { FlagClickedService } from './flag-clicked.service';

describe('FlagClickedService', () => {
  let service: FlagClickedService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FlagClickedService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
