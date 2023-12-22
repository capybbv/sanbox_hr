import { TestBed } from '@angular/core/testing';

import { sampleWithRequiredData, sampleWithNewData } from '../countries.test-samples';

import { CountriesFormService } from './countries-form.service';

describe('Countries Form Service', () => {
  let service: CountriesFormService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(CountriesFormService);
  });

  describe('Service methods', () => {
    describe('createCountriesFormGroup', () => {
      it('should create a new form with FormControl', () => {
        const formGroup = service.createCountriesFormGroup();

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            countryId: expect.any(Object),
            countryName: expect.any(Object),
            countrRegFk: expect.any(Object),
          }),
        );
      });

      it('passing ICountries should create a new form with FormGroup', () => {
        const formGroup = service.createCountriesFormGroup(sampleWithRequiredData);

        expect(formGroup.controls).toEqual(
          expect.objectContaining({
            id: expect.any(Object),
            countryId: expect.any(Object),
            countryName: expect.any(Object),
            countrRegFk: expect.any(Object),
          }),
        );
      });
    });

    describe('getCountries', () => {
      it('should return NewCountries for default Countries initial value', () => {
        const formGroup = service.createCountriesFormGroup(sampleWithNewData);

        const countries = service.getCountries(formGroup) as any;

        expect(countries).toMatchObject(sampleWithNewData);
      });

      it('should return NewCountries for empty Countries initial value', () => {
        const formGroup = service.createCountriesFormGroup();

        const countries = service.getCountries(formGroup) as any;

        expect(countries).toMatchObject({});
      });

      it('should return ICountries', () => {
        const formGroup = service.createCountriesFormGroup(sampleWithRequiredData);

        const countries = service.getCountries(formGroup) as any;

        expect(countries).toMatchObject(sampleWithRequiredData);
      });
    });

    describe('resetForm', () => {
      it('passing ICountries should not enable id FormControl', () => {
        const formGroup = service.createCountriesFormGroup();
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, sampleWithRequiredData);

        expect(formGroup.controls.id.disabled).toBe(true);
      });

      it('passing NewCountries should disable id FormControl', () => {
        const formGroup = service.createCountriesFormGroup(sampleWithRequiredData);
        expect(formGroup.controls.id.disabled).toBe(true);

        service.resetForm(formGroup, { id: null });

        expect(formGroup.controls.id.disabled).toBe(true);
      });
    });
  });
});
