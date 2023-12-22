import { ILocations } from 'app/entities/locations/locations.model';
import { IRegion } from 'app/entities/region/region.model';

export interface ICountries {
  id: number;
  countryId?: string | null;
  countryName?: string | null;
  locations?: ILocations[] | null;
  countrRegFk?: IRegion | null;
}

export type NewCountries = Omit<ICountries, 'id'> & { id: null };
