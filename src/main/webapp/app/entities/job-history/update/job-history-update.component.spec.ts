import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { IDepartments } from 'app/entities/departments/departments.model';
import { DepartmentsService } from 'app/entities/departments/service/departments.service';
import { IJobs } from 'app/entities/jobs/jobs.model';
import { JobsService } from 'app/entities/jobs/service/jobs.service';
import { IJobHistory } from '../job-history.model';
import { JobHistoryService } from '../service/job-history.service';
import { JobHistoryFormService } from './job-history-form.service';

import { JobHistoryUpdateComponent } from './job-history-update.component';

describe('JobHistory Management Update Component', () => {
  let comp: JobHistoryUpdateComponent;
  let fixture: ComponentFixture<JobHistoryUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let jobHistoryFormService: JobHistoryFormService;
  let jobHistoryService: JobHistoryService;
  let departmentsService: DepartmentsService;
  let jobsService: JobsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), JobHistoryUpdateComponent],
      providers: [
        FormBuilder,
        {
          provide: ActivatedRoute,
          useValue: {
            params: from([{}]),
          },
        },
      ],
    })
      .overrideTemplate(JobHistoryUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(JobHistoryUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    jobHistoryFormService = TestBed.inject(JobHistoryFormService);
    jobHistoryService = TestBed.inject(JobHistoryService);
    departmentsService = TestBed.inject(DepartmentsService);
    jobsService = TestBed.inject(JobsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should call Departments query and add missing value', () => {
      const jobHistory: IJobHistory = { id: 456 };
      const jhistDepFk: IDepartments = { id: 15672 };
      jobHistory.jhistDepFk = jhistDepFk;

      const departmentsCollection: IDepartments[] = [{ id: 25773 }];
      jest.spyOn(departmentsService, 'query').mockReturnValue(of(new HttpResponse({ body: departmentsCollection })));
      const additionalDepartments = [jhistDepFk];
      const expectedCollection: IDepartments[] = [...additionalDepartments, ...departmentsCollection];
      jest.spyOn(departmentsService, 'addDepartmentsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ jobHistory });
      comp.ngOnInit();

      expect(departmentsService.query).toHaveBeenCalled();
      expect(departmentsService.addDepartmentsToCollectionIfMissing).toHaveBeenCalledWith(
        departmentsCollection,
        ...additionalDepartments.map(expect.objectContaining),
      );
      expect(comp.departmentsSharedCollection).toEqual(expectedCollection);
    });

    it('Should call Jobs query and add missing value', () => {
      const jobHistory: IJobHistory = { id: 456 };
      const jhistJob: IJobs = { id: 28519 };
      jobHistory.jhistJob = jhistJob;

      const jobsCollection: IJobs[] = [{ id: 23619 }];
      jest.spyOn(jobsService, 'query').mockReturnValue(of(new HttpResponse({ body: jobsCollection })));
      const additionalJobs = [jhistJob];
      const expectedCollection: IJobs[] = [...additionalJobs, ...jobsCollection];
      jest.spyOn(jobsService, 'addJobsToCollectionIfMissing').mockReturnValue(expectedCollection);

      activatedRoute.data = of({ jobHistory });
      comp.ngOnInit();

      expect(jobsService.query).toHaveBeenCalled();
      expect(jobsService.addJobsToCollectionIfMissing).toHaveBeenCalledWith(jobsCollection, ...additionalJobs.map(expect.objectContaining));
      expect(comp.jobsSharedCollection).toEqual(expectedCollection);
    });

    it('Should update editForm', () => {
      const jobHistory: IJobHistory = { id: 456 };
      const jhistDepFk: IDepartments = { id: 25540 };
      jobHistory.jhistDepFk = jhistDepFk;
      const jhistJob: IJobs = { id: 2017 };
      jobHistory.jhistJob = jhistJob;

      activatedRoute.data = of({ jobHistory });
      comp.ngOnInit();

      expect(comp.departmentsSharedCollection).toContain(jhistDepFk);
      expect(comp.jobsSharedCollection).toContain(jhistJob);
      expect(comp.jobHistory).toEqual(jobHistory);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJobHistory>>();
      const jobHistory = { id: 123 };
      jest.spyOn(jobHistoryFormService, 'getJobHistory').mockReturnValue(jobHistory);
      jest.spyOn(jobHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jobHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: jobHistory }));
      saveSubject.complete();

      // THEN
      expect(jobHistoryFormService.getJobHistory).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(jobHistoryService.update).toHaveBeenCalledWith(expect.objectContaining(jobHistory));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJobHistory>>();
      const jobHistory = { id: 123 };
      jest.spyOn(jobHistoryFormService, 'getJobHistory').mockReturnValue({ id: null });
      jest.spyOn(jobHistoryService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jobHistory: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: jobHistory }));
      saveSubject.complete();

      // THEN
      expect(jobHistoryFormService.getJobHistory).toHaveBeenCalled();
      expect(jobHistoryService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJobHistory>>();
      const jobHistory = { id: 123 };
      jest.spyOn(jobHistoryService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jobHistory });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(jobHistoryService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });

  describe('Compare relationships', () => {
    describe('compareDepartments', () => {
      it('Should forward to departmentsService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(departmentsService, 'compareDepartments');
        comp.compareDepartments(entity, entity2);
        expect(departmentsService.compareDepartments).toHaveBeenCalledWith(entity, entity2);
      });
    });

    describe('compareJobs', () => {
      it('Should forward to jobsService', () => {
        const entity = { id: 123 };
        const entity2 = { id: 456 };
        jest.spyOn(jobsService, 'compareJobs');
        comp.compareJobs(entity, entity2);
        expect(jobsService.compareJobs).toHaveBeenCalledWith(entity, entity2);
      });
    });
  });
});
