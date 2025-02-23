import { ComponentFixture, TestBed } from '@angular/core/testing';

import { FlagItemComponent } from './flag-item.component';

describe('FlagItemComponent', () => {
  let component: FlagItemComponent;
  let fixture: ComponentFixture<FlagItemComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [FlagItemComponent]
    })
    .compileComponents();

    fixture = TestBed.createComponent(FlagItemComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
