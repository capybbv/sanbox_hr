import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { JobsDetailComponent } from './jobs-detail.component';

describe('Jobs Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [JobsDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: JobsDetailComponent,
              resolve: { jobs: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(JobsDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load jobs on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', JobsDetailComponent);

      // THEN
      expect(instance.jobs).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
