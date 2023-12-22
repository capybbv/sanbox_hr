import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { ICountries } from 'app/entities/countries/countries.model';
import { CountriesService } from 'app/entities/countries/service/countries.service';
import { LocationsService } from '../service/locations.service';
import { ILocations } from '../locations.model';
import { LocationsFormService } from './locations-form.service';

import { LocationsUpdateComponent } from './locations-update.component';

describe('Locations Management Update Component', () => {
  let comp: LocationsUpdateComponent;
  let fixture: ComponentFixture<LocationsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let locationsFormService: LocationsFormService;
  let locationsService: LocationsService;
  let countriesService: CountriesService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), LocationsUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(LocationsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(LocationsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    locationsFormService = TestBed.inject(LocationsFormService);
    locationsService = TestBed.inject(LocationsService);
    countriesService = TestBed.inject(CountriesService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Countries query and add missing value', () => {
      const locations: ILocations = { id: 456 };
      const locCIdFk: ICountries = { id: 22263 };
      locations.locCIdFk = locCIdFk;

      const countriesCollection: ICountries[] = [{ id: 22003 }];
      jest.spyOn(countriesService, 'query').mockReturnValue(of(new HttpResponse({ body: countriesCollection })));
      const additionalCountries = [locCIdFk];
      const expectedCollection: ICountries[] = [...additionalCountries, ...countriesCollection];
      jest.spyOn(countriesService, 'addCountriesToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ locations });
      comp.ngOnInit();

      expect(countriesService.query).toHaveBeenCalled();
      expect(countriesService.addCountriesToCollectionIfMissing).toHaveBeenCalledWith(
        countriesCollection,
        ...additionalCountries.map(expect.objectContaining),
      );
      expect(comp.countriesSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const locations: ILocations = { id: 456 };
      const locCIdFk: ICountries = { id: 19420 };
      locations.locCIdFk = locCIdFk;

      activatedRoute.data = of({ locations });
      comp.ngOnInit();

      expect(comp.countriesSharedCollection).toContain(locCIdFk);
      expect(comp.locations).toEqual(locations);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILocations>>();
      const locations = { id: 123 };
      jest.spyOn(locationsFormService, 'getLocations').mockReturnValue(locations);
      jest.spyOn(locationsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ locations });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: locations }));
      saveSubject.complete();

      // THEN
      expect(locationsFormService.getLocations).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(locationsService.update).toHaveBeenCalledWith(expect.objectContaining(locations));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILocations>>();
      const locations = { id: 123 };
      jest.spyOn(locationsFormService, 'getLocations').mockReturnValue({ id: null });
      jest.spyOn(locationsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ locations: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: locations }));
      saveSubject.complete();

      // THEN
      expect(locationsFormService.getLocations).toHaveBeenCalled();
      expect(locationsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<ILocations>>();
      const locations = { id: 123 };
      jest.spyOn(locationsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ locations });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(locationsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareCountries', () => {
      it('Should forward to countriesService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(countriesService, 'compareCountries');
        comp.compareCountries(entity, entity2);
        expect(countriesService.compareCountries).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
