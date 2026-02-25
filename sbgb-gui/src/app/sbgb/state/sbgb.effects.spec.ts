import {of, throwError} from 'rxjs';
import {Actions} from '@ngrx/effects';
import {SbgbEffects} from './sbgb.effects';
import {ImageApiActions, SbgbPageActions} from './sbgb.actions';
import {NoiseCosmeticRenderDto} from '../sbgb.model';

const aRender = (): NoiseCosmeticRenderDto => ({
  id: 'render-1', baseStructureId: 'base-1', description: 'desc',
  note: 3, back: '#000', middle: '#888', fore: '#FFF',
  backThreshold: 0.4, middleThreshold: 0.7,
  interpolationType: 'LINEAR', transparentBackground: false, thumbnail: null
});

const mockService = () => ({
  buildImage: jest.fn(),
  getBases: jest.fn(),
  rateRender: jest.fn(),
  deleteRender: jest.fn(),
  getRendersForBase: jest.fn()
});

describe('SbgbEffects', () => {
  describe('loadRendersForBase$', () => {
    it('should dispatch imagesRendersLoadSuccess on success', (done) => {
      const renders = [aRender()];
      const service = mockService();
      service.getRendersForBase.mockReturnValue(of(renders));
      const action = SbgbPageActions.loadRendersForBase({baseId: 'base-1'});
      const actions$ = new Actions(of(action));
      const effects = new SbgbEffects(service as any, actions$, null as any);

      effects.loadRendersForBase$.subscribe((dispatched: any) => {
        expect(dispatched).toEqual(ImageApiActions.imagesRendersLoadSuccess({renders}));
        done();
      });
    });

    it('should dispatch imagesRendersLoadFail on error', (done) => {
      const service = mockService();
      service.getRendersForBase.mockReturnValue(throwError(() => new Error('Network error')));
      const action = SbgbPageActions.loadRendersForBase({baseId: 'base-1'});
      const actions$ = new Actions(of(action));
      const effects = new SbgbEffects(service as any, actions$, null as any);

      effects.loadRendersForBase$.subscribe((dispatched: any) => {
        expect(dispatched).toEqual(ImageApiActions.imagesRendersLoadFail({message: 'Network error'}));
        done();
      });
    });
  });

  describe('deleteRender$', () => {
    it('should dispatch imagesDeleteRenderSuccess on success', (done) => {
      const service = mockService();
      service.deleteRender.mockReturnValue(of(undefined));
      const action = SbgbPageActions.deleteRender({renderId: 'render-1'});
      const actions$ = new Actions(of(action));
      const effects = new SbgbEffects(service as any, actions$, null as any);

      effects.deleteRender$.subscribe((dispatched: any) => {
        expect(dispatched).toEqual(ImageApiActions.imagesDeleteRenderSuccess({renderId: 'render-1'}));
        done();
      });
    });

    it('should dispatch imagesDeleteRenderFail on error', (done) => {
      const service = mockService();
      service.deleteRender.mockReturnValue(throwError(() => ({message: 'Server error'})));
      const action = SbgbPageActions.deleteRender({renderId: 'render-1'});
      const actions$ = new Actions(of(action));
      const effects = new SbgbEffects(service as any, actions$, null as any);

      effects.deleteRender$.subscribe((dispatched: any) => {
        expect(dispatched).toEqual(ImageApiActions.imagesDeleteRenderFail({message: 'Server error'}));
        done();
      });
    });
  });
});
