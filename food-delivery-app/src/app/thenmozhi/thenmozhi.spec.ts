import { ComponentFixture, TestBed } from '@angular/core/testing';

import { Thenmozhi } from './thenmozhi';

describe('Thenmozhi', () => {
  let component: Thenmozhi;
  let fixture: ComponentFixture<Thenmozhi>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [Thenmozhi]
    })
    .compileComponents();

    fixture = TestBed.createComponent(Thenmozhi);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
