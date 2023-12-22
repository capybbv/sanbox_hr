import { IDepartments } from 'app/entities/departments/departments.model';
import { ICountries } from 'app/entities/countries/countries.model';

export interface ILocations {
  id: number;
  locationId?: number | null;
  streetAddress?: string | null;
  postalCode?: string | null;
  city?: string | null;
  stateProvince?: string | null;
  departments?: IDepartments[] | null;
  locCIdFk?: ICountries | null;
}

export type NewLocations = Omit<ILocations, 'id'> & { id: null };
