import { ICountries, NewCountries } from './countries.model';

export const sampleWithRequiredData: ICountries = {
  id: 28844,
  countryId: 'no',
};

export const sampleWithPartialData: ICountries = {
  id: 28499,
  countryId: 'yo',
};

export const sampleWithFullData: ICountries = {
  id: 21861,
  countryId: 'ho',
  countryName: 'within',
};

export const sampleWithNewData: NewCountries = {
  countryId: 'su',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
