/** GTP execution machinery.
 * <p>Execution is spilt into 3 {@link ExecutionLoop}s each handled by it's
 * dedicated thread. The loops are inter-connected. Loop takes task and returns
 * {@code Future} for its outcome. The task is executed asynchronously and
 * passed to next execution loop. If the task carries {@link Sync} train loop
 * waits for outcome of next loop before exposing the outcome via Future.</>
 *
 * <p>{@link CommandReadingLoop} reads command lines from
 * {@link GtpTransport#commands()}</p> channel, uses {@link CommandParser} to
 * turn them into {@link Action}s which then feeds to next processing loop.
 * It has no queue and produces no futures of it's own. Instead it uses
 * futures returned next loop.</p>
 *
 * <p>{@link ActionProcessingLoop} calls {@link Action#execute(Context)} and
 * pases retults to the next loop. If the execution fails it wraps exception
 * into {@link Result.Failure}. If also implements logic for the {@Cancelable}
 * and {@link Canceling} traints and waits for completion of downstream futures
 * for {@link Sync} trains.</p>
 *
 * <p>{@link ResultWritingLoop} formats results and writes them to
 * {@link GtpTransport#responses()}.</p>
 *
 * <p>All loops will terminate if submitted task caries {@link Shutdown} trait.
 * Taks submitted before shutdown are still executed. Attempts to subnit task
 * after shutdow will throw {@link SubmitRejectedException}. When all tasks
 * are processed the exxecution loop will call calback passed to the constructor.
 * Shutdown is propagated downstrean. When execution loop fails then it will
 * end immediatelly. Taks in queue are cancelled, shutdoew is sent downstream
 * submit attempts will fail. For reading/writing task it might be I/O. For
 * action task it wold have to be error (OutOfMemoryError for example) or
 * exception propagated from downstream (rejected submit). Exceptions from
 * actions are handled.</p>
 */
package cz.doleckovi.piskvorky.gtp.execution;

import cz.doleckovi.piskvorky.gtp.action.Action;
import cz.doleckovi.piskvorky.gtp.action.Context;
import cz.doleckovi.piskvorky.gtp.action.Result;
import cz.doleckovi.piskvorky.gtp.parser.CommandParser;
import cz.doleckovi.piskvorky.gtp.trait.Canceling;
import cz.doleckovi.piskvorky.gtp.trait.Shutdown;
import cz.doleckovi.piskvorky.gtp.trait.Sync;
import cz.doleckovi.piskvorky.gtp.transport.GtpTransport;
