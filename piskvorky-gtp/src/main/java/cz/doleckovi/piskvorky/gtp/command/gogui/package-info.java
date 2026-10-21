/**
 * GoGui engine extension commands beyond the GTP standard set.
 *
 * <p>{@link cz.doleckovi.piskvorky.gtp.command.gogui.GoGuiCommandFactory} assembles
 * the command bodies defined here (currently the interrupt advertisement pair of
 * GoGui manual ch. 10); the fixed rule queries form their own family in the
 * {@link cz.doleckovi.piskvorky.gtp.command.gogui.rules rules subpackage}. A
 * {@link cz.doleckovi.piskvorky.gtp.command.CompositeCommandFactory} combines these
 * factories with the standard GTP ones.</p>
 */
package cz.doleckovi.piskvorky.gtp.command.gogui;
