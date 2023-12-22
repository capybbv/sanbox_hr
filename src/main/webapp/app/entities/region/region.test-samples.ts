import { IRegion, NewRegion } from './region.model';

export const sampleWithRequiredData: IRegion = {
  id: 13690,
  regionId: 26895,
};

export const sampleWithPartialData: IRegion = {
  id: 11595,
  regionId: 9609,
};

export const sampleWithFullData: IRegion = {
  id: 14933,
  regionId: 6559,
  regionName: 'oh',
};

export const sampleWithNewData: NewRegion = {
  regionId: 2084,
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
