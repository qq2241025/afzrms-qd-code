/**
 * @class Ext.form.ColorField
 * @extends Ext.form.TriggerField
 * Provides a very simple color form field with a ColorMenu dropdown.
 * Values are stored as a six-character hex value without the '#'.
 * I.e. 'ffffff'
 * @constructor
 * Create a new ColorField
 * <br />Example:
 * <pre><code>
var cf = new Ext.form.ColorField({
    fieldLabel: 'Color',
    hiddenName:'pref_sales',
    showHexValue:true
});
</code></pre>
 * @param {Object} config
 */
Ext.form.ColorField = function(config){
    Ext.form.ColorField.superclass.constructor.call(this, config);
    //this.on('render', this.handleRender);
};
 
Ext.ColorPalette.prototype.colors =  [
    '000000', '993300', '333300', '003300', '003366', '000080', '333399', '333333',
    '800000', 'FF6600', '808000', '008000', '008080', '0000FF', '666699', '808080',
    '467500', '000079', '003E3E', '006030', '009100', '0000C6', 'C4E1FF', 'ECFFFF',
    '750000', '5B5B00', 'D9B300', 'FF9224', 'FF8040', 'C2FF68', 'FFFFAA', 'DEFFAC',
    '4B0091', '8600FF', '6C3365', '408080', 'AD5A5A', 'F2E6E6', 'EBD3E8', '336666',
    'FF0000', 'FF9900', '99CC00', '339966', '33CCCC', '3366FF', '800080', '969696',
    'FF00FF', 'FFCC00', 'FFFF00', '00FF00', '00FFFF', '00CCFF', '993366', 'C0C0C0',
    'FF99CC', 'FFCC99', 'FFFF99', 'CCFFCC', 'CCFFFF', '99CCFF', 'CC99FF', 'FFFFFF'
];
Ext.extend(Ext.form.ColorField, Ext.form.TriggerField,  {
    /**
     * @cfg {Boolean} showHexValue
     * True to display the HTML Hexidecimal Color Value in the field
     * so it is manually editable.
     */
    showHexValue : false,
    
    /**
     * @cfg {String} triggerClass
     * An additional CSS class used to style the trigger button.  The trigger will always get the
     * class 'x-form-trigger' and triggerClass will be <b>appended</b> if specified (defaults to 'x-form-color-trigger'
     * which displays a calendar icon).
     */
    triggerClass : 'x-form-color-trigger',
    
    /**
     * @cfg {String/Object} autoCreate
     * A DomHelper element spec, or true for a default element spec (defaults to
     * {tag: "input", type: "text", size: "10", autocomplete: "off"})
     */
    // private
    defaultAutoCreate : {tag: "input", type: "text", size: "10",
                         autocomplete: "off", maxlength:"1"},
    
    /**
     * @cfg {String} lengthText
     * A string to be displayed when the length of the input field is
     * not 3 or 6, i.e. 'fff' or 'ffccff'.
     */
    lengthText: "Color hex values must be either 3 or 6 characters.",
    
    //text to use if blank and allowBlank is false
    blankText: "Must have a hexidecimal value in the format ABCDEF.",
    
    /**
     * @cfg {String} color
     * A string hex value to be used as the default color.  Defaults
     * to 'FFFFFF' (white).
     */
    defaultColor: 'FFFFFF',
    
    maskRe: /[a-f0-9]/i,
    // These regexes limit input and validation to hex values
    regex: /[a-f0-9]/i,
 
    //private
    curColor: 'ffffff',
    initComponent:function(){   
        Ext.form.ColorField.superclass.initComponent.call(this);   
        this.addEvents('click','change', 'select');  
    },        
    onChange: function (field, newVal, oldVal) {
        alert(newVal);
    },
    onRender : function(ct, position){
        Ext.form.ColorField.superclass.onRender.call(this, ct, position);
        this.handleRender();
    },
    // private
    validateValue : function(value){
        if(!this.showHexValue) {
            return true;
        }
        if(value.length<1) {
            this.el.setStyle({
                'background-color':'#' + this.defaultColor
            });
            if(!this.allowBlank) {
                this.markInvalid(String.format(this.blankText, value));
                return false
            }
            return true;
        }
        if(value.length!=3 && value.length!=6 ) {
            this.markInvalid(String.format(this.lengthText, value));
            return false;
        }
        this.setColor(value);
        return true;
    },
 
    // private
    validateBlur : function(){
        return !this.menu || !this.menu.isVisible();
    },
    
    // Manually apply the invalid line image since the background
    // was previously cleared so the color would show through.
    markInvalid : function( msg ) {
        Ext.form.ColorField.superclass.markInvalid.call(this, msg);
        this.el.setStyle({
            'background-image': 'url(../lib/resources/images/default/grid/invalid_line.gif)'
        });
    },
 
    /**
     * Returns the current color value of the color field
     * @return {String} value The hexidecimal color value
     */
    getValue : function(){
        return this.curValue || this.defaultValue || "FFFFFF";
    },
 
    /**
     * Sets the value of the color field.  Format as hex value 'FFFFFF'
     * without the '#'.
     * @param {String} hex The color value
     */
    setValue : function(hex){
        Ext.form.ColorField.superclass.setValue.call(this, hex);
        this.setColor(hex);
    },
    
    /**
     * Sets the current color and changes the background.
     * Does *not* change the value of the field.
     * @param {String} hex The color value.
     */
    setColor : function(hex) {
        this.curColor = hex;
        
        this.el.setStyle( {
            'background-color': '#' + hex,
            'background-image': 'none'
        });
        if(!this.showHexValue) {
            this.el.setStyle({
                'text-indent': '-100px'
            });
            if(Ext.isIE) {
                this.el.setStyle({
                    'margin-left': '100px'
                });
            }
        }
    },
    
    handleRender: function() {
        this.setDefaultColor();
    },
    
    setDefaultColor : function() {
        this.setValue(this.defaultColor);
    },
 
    // private
    menuListeners : {
        select: function(m, d){
            this.setValue(d);
        },
        show : function(){ // retain focus styling
            this.onFocus();
        },
        hide : function(){
            this.focus();
            var ml = this.menuListeners;
            this.menu.un("select", ml.select,  this);
            this.menu.un("show", ml.show,  this);
            this.menu.un("hide", ml.hide,  this);
        }
    },
    
    //private
    handleSelect : function(palette, selColor) {
        this.setValue(selColor);
        this.fireEvent("click", this, selColor);        
        this.fireEvent("change", this, selColor);        
        this.fireEvent("select", this, selColor); 
    },
 
    // private
    // Implements the default empty TriggerField.onTriggerClick function to display the ColorPicker
    onTriggerClick : function(){
        if(this.disabled){
            return;
        }
        if(this.menu == null){
            this.menu = new Ext.menu.ColorMenu();
            this.menu.palette.on('select', this.handleSelect, this );
        }
        this.menu.on(Ext.apply({}, this.menuListeners, {
            scope:this
        }));
        this.menu.show(this.el, "tl-bl?");
    }
});