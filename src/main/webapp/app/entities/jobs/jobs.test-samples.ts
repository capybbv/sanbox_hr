import { IJobs, NewJobs } from './jobs.model';

export const sampleWithRequiredData: IJobs = {
  id: 15339,
  jobId: 20915,
  jobTitle: 'National Implementation Analyst',
};

export const sampleWithPartialData: IJobs = {
  id: 28935,
  jobId: 10480,
  jobTitle: 'Lead Tactics Strategist',
  minSalary: 7203,
  maxSalary: 29931,
};

export const sampleWithFullData: IJobs = {
  id: 28720,
  jobId: 10179,
  jobTitle: 'National Tactics Representative',
  minSalary: 24283,
  maxSalary: 8827,
};

export const sampleWithNewData: NewJobs = {
  jobId: 804,
  jobTitle: 'Regional Data Officer',
  id: null,
};

Object.freeze(sampleWithNewData);
Object.freeze(sampleWithRequiredData);
Object.freeze(sampleWithPartialData);
Object.freeze(sampleWithFullData);
