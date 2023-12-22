import { ILocations, NewLocations } from './locations.model';

export const sampleWithRequiredData: ILocations = {
  id: 7648,
  locationId: 13886,
};

export const sampleWithPartialData: ILocations = {
  id: 3679,
  locationId: 5601,
  streetAddress: 'bedeck ha',
  city: 'Flower Mound',
};

export const sampleWithFullData: ILocations = {
  id: 31386,
  locationId: 706,
  streetAddress: 'bravely',
  postalCode: 'packetise wh',
  city: 'Redlands',
  stateProvince: 'like along',
};

export const sampleWithNewData: NewLocations = {
  locationId: 28333,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
