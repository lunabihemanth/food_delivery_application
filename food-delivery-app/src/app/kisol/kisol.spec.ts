import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Kisol } from './kisol';

describe('Kisol', () => {
  let component: Kisol;
  let fixture: ComponentFixture<Kisol>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Kisol]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Kisol);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
