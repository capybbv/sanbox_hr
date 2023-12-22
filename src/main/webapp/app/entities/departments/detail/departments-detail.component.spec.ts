import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { DepartmentsDetailComponent } from './departments-detail.component';

describe('Departments Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [DepartmentsDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: DepartmentsDetailComponent,
              resolve: { departments: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(DepartmentsDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load departments on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', DepartmentsDetailComponent);

      // THEN
      expect(instance.departments).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
