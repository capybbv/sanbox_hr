import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { EmployeesDetailComponent } from './employees-detail.component';

describe('Employees Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EmployeesDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: EmployeesDetailComponent,
              resolve: { employees: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(EmployeesDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load employees on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', EmployeesDetailComponent);

      // THEN
      expect(instance.employees).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
