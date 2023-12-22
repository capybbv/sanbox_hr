import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { FormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';
import { of, Subject, from } from 'rxjs';

import { JobsService } from '../service/jobs.service';
import { IJobs } from '../jobs.model';
import { JobsFormService } from './jobs-form.service';

import { JobsUpdateComponent } from './jobs-update.component';

describe('Jobs Management Update Component', () => {
  let comp: JobsUpdateComponent;
  let fixture: ComponentFixture<JobsUpdateComponent>;
  let activatedRoute: ActivatedRoute;
  let jobsFormService: JobsFormService;
  let jobsService: JobsService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule, RouterTestingModule.withRoutes([]), JobsUpdateComponent],
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
      .overrideTemplate(JobsUpdateComponent, '')
      .compileComponents();

    fixture = TestBed.createComponent(JobsUpdateComponent);
    activatedRoute = TestBed.inject(ActivatedRoute);
    jobsFormService = TestBed.inject(JobsFormService);
    jobsService = TestBed.inject(JobsService);

    comp = fixture.componentInstance;
  });

  describe('ngOnInit', () => {
    it('Should update editForm', () => {
      const jobs: IJobs = { id: 456 };

      activatedRoute.data = of({ jobs });
      comp.ngOnInit();

      expect(comp.jobs).toEqual(jobs);
    });
  });

  describe('save', () => {
    it('Should call update service on save for existing entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJobs>>();
      const jobs = { id: 123 };
      jest.spyOn(jobsFormService, 'getJobs').mockReturnValue(jobs);
      jest.spyOn(jobsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jobs });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: jobs }));
      saveSubject.complete();

      // THEN
      expect(jobsFormService.getJobs).toHaveBeenCalled();
      expect(comp.previousState).toHaveBeenCalled();
      expect(jobsService.update).toHaveBeenCalledWith(expect.objectContaining(jobs));
      expect(comp.isSaving).toEqual(false);
    });

    it('Should call create service on save for new entity', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJobs>>();
      const jobs = { id: 123 };
      jest.spyOn(jobsFormService, 'getJobs').mockReturnValue({ id: null });
      jest.spyOn(jobsService, 'create').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jobs: null });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.next(new HttpResponse({ body: jobs }));
      saveSubject.complete();

      // THEN
      expect(jobsFormService.getJobs).toHaveBeenCalled();
      expect(jobsService.create).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).toHaveBeenCalled();
    });

    it('Should set isSaving to false on error', () => {
      // GIVEN
      const saveSubject = new Subject<HttpResponse<IJobs>>();
      const jobs = { id: 123 };
      jest.spyOn(jobsService, 'update').mockReturnValue(saveSubject);
      jest.spyOn(comp, 'previousState');
      activatedRoute.data = of({ jobs });
      comp.ngOnInit();

      // WHEN
      comp.save();
      expect(comp.isSaving).toEqual(true);
      saveSubject.error('This is an error!');

      // THEN
      expect(jobsService.update).toHaveBeenCalled();
      expect(comp.isSaving).toEqual(false);
      expect(comp.previousState).not.toHaveBeenCalled();
    });
  });
});
