import { TestBed } from '@angular/core/testing';
import { provideRouter, withComponentInputBinding } from '@angular/router';
import { RouterTestingHarness, RouterTestingModule } from '@angular/router/testing';
import { of } from 'rxjs';

import { LocationsDetailComponent } from './locations-detail.component';

describe('Locations Management Detail Component', () => {
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LocationsDetailComponent, RouterTestingModule.withRoutes([], { bindToComponentInputs: true })],
      providers: [
        provideRouter(
          [
            {
              path: '**',
              component: LocationsDetailComponent,
              resolve: { locations: () => of({ id: 123 }) },
            },
          ],
          withComponentInputBinding(),
        ),
      ],
    })
      .overrideTemplate(LocationsDetailComponent, '')
      .compileComponents();
  });

  describe('OnInit', () => {
    it('Should load locations on init', async () => {
      const harness = await RouterTestingHarness.create();
      const instance = await harness.navigateByUrl('/', LocationsDetailComponent);

      // THEN
      expect(instance.locations).toEqual(expect.objectContaining({ id: 123 }));
    });
  });
});
