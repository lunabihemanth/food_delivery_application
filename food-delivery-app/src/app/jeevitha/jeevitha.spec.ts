import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Jeevitha } from './jeevitha';

describe('Jeevitha', () => {
  let component: Jeevitha;
  let fixture: ComponentFixture<Jeevitha>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Jeevitha]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Jeevitha);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
