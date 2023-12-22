import { ICountries } from 'app/entities/countries/countries.model';

export interface IRegion {
  id: number;
  regionId?: number | null;
  regionName?: string | null;
  countries?: ICountries[] | null;
}

export type NewRegion = Omit<IRegion, 'id'> & { id: null };
