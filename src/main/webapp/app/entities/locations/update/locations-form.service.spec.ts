import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../locations.test-samples';

import { LocationsFormService } from './locations-form.service';

describe('Locations Form Service', () => {
  let service: LocationsFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(LocationsFormService);
  });

  describe('Service methods', () => {
    describe('createLocationsFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createLocationsFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            locationId: expect.any(Object),
            streetAddress: expect.any(Object),
            postalCode: expect.any(Object),
            city: expect.any(Object),
            stateProvince: expect.any(Object),
            locCIdFk: expect.any(Object),
          }),
        );
      });

      it('passing ILocations should create a new form with FormGroup', () => {
        const formGroup = service.createLocationsFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            locationId: expect.any(Object),
            streetAddress: expect.any(Object),
            postalCode: expect.any(Object),
            city: expect.any(Object),
            stateProvince: expect.any(Object),
            locCIdFk: expect.any(Object),
          }),
        );
      });
    });

    describe('getLocations', () => {
      it('should return NewLocations for default Locations initial value', () => {
        const formGroup = service.createLocationsFormGroup(sampleWithNewData);

        const locations = service.getLocations(formGroup) as any;

        expect(locations).toMatchObject(sampleWithNewData);
      });

      it('should return NewLocations for empty Locations initial value', () => {
        const formGroup = service.createLocationsFormGroup();

        const locations = service.getLocations(formGroup) as any;

        expect(locations).toMatchObject({});
      });

      it('should return ILocations', () => {
        const formGroup = service.createLocationsFormGroup(sampleWithRequiredData);

        const locations = service.getLocations(formGroup) as any;

        expect(locations).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ILocations should not enable id FormControl', () => {
        const formGroup = service.createLocationsFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewLocations should disable id FormControl', () => {
        const formGroup = service.createLocationsFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
