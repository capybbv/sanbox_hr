import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { CountriesDetailComponent } from './countries-detail.component';

describe('Countries Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CountriesDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: CountriesDetailComponent,
              resolve: { countries: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(CountriesDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load countries on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', CountriesDetailComponent);

      // THEN
      expect(instance.countries).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
