import { TestBed } from '@angular/core/testing';

import { FlagService } from './flags-api-service.service';

describe('FlagsApiServiceService', () => {
  let service: FlagService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(FlagService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
