import dayjs from 'dayjs/esm';

import { IJobHistory, NewJobHistory } from './job-history.model';

export const sampleWithRequiredData: IJobHistory = {
  id: 16251,
  employId: 691,
  startDate: dayjs('2023-12-21T22:22'),
  endDate: dayjs('2023-12-21T08:09'),
};

export const sampleWithPartialData: IJobHistory = {
  id: 10977,
  employId: 21154,
  startDate: dayjs('2023-12-21T14:43'),
  endDate: dayjs('2023-12-21T11:58'),
};

export const sampleWithFullData: IJobHistory = {
  id: 22624,
  employId: 15729,
  startDate: dayjs('2023-12-21T05:32'),
  endDate: dayjs('2023-12-21T20:27'),
};

export const sampleWithNewData: NewJobHistory = {
  employId: 27771,
  startDate: dayjs('2023-12-22T03:52'),
  endDate: dayjs('2023-12-21T11:56'),
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
