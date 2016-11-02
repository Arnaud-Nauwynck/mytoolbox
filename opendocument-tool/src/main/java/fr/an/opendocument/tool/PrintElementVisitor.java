package fr.an.opendocument.tool;

import org.odftoolkit.odfdom.dom.DefaultElementVisitor;
import org.odftoolkit.odfdom.dom.element.anim.AnimAnimateColorElement;
import org.odftoolkit.odfdom.dom.element.anim.AnimAnimateElement;
import org.odftoolkit.odfdom.dom.element.anim.AnimAnimateMotionElement;
import org.odftoolkit.odfdom.dom.element.anim.AnimAnimateTransformElement;
import org.odftoolkit.odfdom.dom.element.anim.AnimAudioElement;
import org.odftoolkit.odfdom.dom.element.anim.AnimCommandElement;
import org.odftoolkit.odfdom.dom.element.anim.AnimIterateElement;
import org.odftoolkit.odfdom.dom.element.anim.AnimParElement;
import org.odftoolkit.odfdom.dom.element.anim.AnimParamElement;
import org.odftoolkit.odfdom.dom.element.anim.AnimSeqElement;
import org.odftoolkit.odfdom.dom.element.anim.AnimSetElement;
import org.odftoolkit.odfdom.dom.element.anim.AnimTransitionFilterElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartAxisElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartCategoriesElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartChartElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartDataLabelElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartDataPointElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartDomainElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartEquationElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartErrorIndicatorElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartFloorElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartFooterElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartGridElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartLabelSeparatorElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartLegendElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartMeanValueElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartPlotAreaElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartRegressionCurveElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartSeriesElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartStockGainMarkerElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartStockLossMarkerElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartStockRangeLineElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartSubtitleElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartSymbolImageElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartTitleElement;
import org.odftoolkit.odfdom.dom.element.chart.ChartWallElement;
import org.odftoolkit.odfdom.dom.element.config.ConfigConfigItemElement;
import org.odftoolkit.odfdom.dom.element.config.ConfigConfigItemMapEntryElement;
import org.odftoolkit.odfdom.dom.element.config.ConfigConfigItemMapIndexedElement;
import org.odftoolkit.odfdom.dom.element.config.ConfigConfigItemMapNamedElement;
import org.odftoolkit.odfdom.dom.element.config.ConfigConfigItemSetElement;
import org.odftoolkit.odfdom.dom.element.db.DbApplicationConnectionSettingsElement;
import org.odftoolkit.odfdom.dom.element.db.DbAutoIncrementElement;
import org.odftoolkit.odfdom.dom.element.db.DbCharacterSetElement;
import org.odftoolkit.odfdom.dom.element.db.DbColumnDefinitionElement;
import org.odftoolkit.odfdom.dom.element.db.DbColumnDefinitionsElement;
import org.odftoolkit.odfdom.dom.element.db.DbColumnElement;
import org.odftoolkit.odfdom.dom.element.db.DbColumnsElement;
import org.odftoolkit.odfdom.dom.element.db.DbComponentCollectionElement;
import org.odftoolkit.odfdom.dom.element.db.DbComponentElement;
import org.odftoolkit.odfdom.dom.element.db.DbConnectionDataElement;
import org.odftoolkit.odfdom.dom.element.db.DbConnectionResourceElement;
import org.odftoolkit.odfdom.dom.element.db.DbDataSourceElement;
import org.odftoolkit.odfdom.dom.element.db.DbDataSourceSettingElement;
import org.odftoolkit.odfdom.dom.element.db.DbDataSourceSettingValueElement;
import org.odftoolkit.odfdom.dom.element.db.DbDataSourceSettingsElement;
import org.odftoolkit.odfdom.dom.element.db.DbDatabaseDescriptionElement;
import org.odftoolkit.odfdom.dom.element.db.DbDelimiterElement;
import org.odftoolkit.odfdom.dom.element.db.DbDriverSettingsElement;
import org.odftoolkit.odfdom.dom.element.db.DbFileBasedDatabaseElement;
import org.odftoolkit.odfdom.dom.element.db.DbFilterStatementElement;
import org.odftoolkit.odfdom.dom.element.db.DbFormsElement;
import org.odftoolkit.odfdom.dom.element.db.DbIndexColumnElement;
import org.odftoolkit.odfdom.dom.element.db.DbIndexColumnsElement;
import org.odftoolkit.odfdom.dom.element.db.DbIndexElement;
import org.odftoolkit.odfdom.dom.element.db.DbIndicesElement;
import org.odftoolkit.odfdom.dom.element.db.DbKeyColumnElement;
import org.odftoolkit.odfdom.dom.element.db.DbKeyColumnsElement;
import org.odftoolkit.odfdom.dom.element.db.DbKeyElement;
import org.odftoolkit.odfdom.dom.element.db.DbKeysElement;
import org.odftoolkit.odfdom.dom.element.db.DbLoginElement;
import org.odftoolkit.odfdom.dom.element.db.DbOrderStatementElement;
import org.odftoolkit.odfdom.dom.element.db.DbQueriesElement;
import org.odftoolkit.odfdom.dom.element.db.DbQueryCollectionElement;
import org.odftoolkit.odfdom.dom.element.db.DbQueryElement;
import org.odftoolkit.odfdom.dom.element.db.DbReportsElement;
import org.odftoolkit.odfdom.dom.element.db.DbSchemaDefinitionElement;
import org.odftoolkit.odfdom.dom.element.db.DbServerDatabaseElement;
import org.odftoolkit.odfdom.dom.element.db.DbTableDefinitionElement;
import org.odftoolkit.odfdom.dom.element.db.DbTableDefinitionsElement;
import org.odftoolkit.odfdom.dom.element.db.DbTableExcludeFilterElement;
import org.odftoolkit.odfdom.dom.element.db.DbTableFilterElement;
import org.odftoolkit.odfdom.dom.element.db.DbTableFilterPatternElement;
import org.odftoolkit.odfdom.dom.element.db.DbTableIncludeFilterElement;
import org.odftoolkit.odfdom.dom.element.db.DbTableRepresentationElement;
import org.odftoolkit.odfdom.dom.element.db.DbTableRepresentationsElement;
import org.odftoolkit.odfdom.dom.element.db.DbTableSettingElement;
import org.odftoolkit.odfdom.dom.element.db.DbTableSettingsElement;
import org.odftoolkit.odfdom.dom.element.db.DbTableTypeElement;
import org.odftoolkit.odfdom.dom.element.db.DbTableTypeFilterElement;
import org.odftoolkit.odfdom.dom.element.db.DbUpdateTableElement;
import org.odftoolkit.odfdom.dom.element.dc.DcCreatorElement;
import org.odftoolkit.odfdom.dom.element.dc.DcDateElement;
import org.odftoolkit.odfdom.dom.element.dc.DcDescriptionElement;
import org.odftoolkit.odfdom.dom.element.dc.DcLanguageElement;
import org.odftoolkit.odfdom.dom.element.dc.DcSubjectElement;
import org.odftoolkit.odfdom.dom.element.dc.DcTitleElement;
import org.odftoolkit.odfdom.dom.element.dr3d.Dr3dCubeElement;
import org.odftoolkit.odfdom.dom.element.dr3d.Dr3dExtrudeElement;
import org.odftoolkit.odfdom.dom.element.dr3d.Dr3dLightElement;
import org.odftoolkit.odfdom.dom.element.dr3d.Dr3dRotateElement;
import org.odftoolkit.odfdom.dom.element.dr3d.Dr3dSceneElement;
import org.odftoolkit.odfdom.dom.element.dr3d.Dr3dSphereElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawAElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawAppletElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawAreaCircleElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawAreaPolygonElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawAreaRectangleElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawCaptionElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawCircleElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawConnectorElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawContourPathElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawContourPolygonElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawControlElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawCustomShapeElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawEllipseElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawEnhancedGeometryElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawEquationElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawFillImageElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawFloatingFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawFrameElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawGElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawGluePointElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawGradientElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawHandleElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawHatchElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawImageMapElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawLayerElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawLayerSetElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawLineElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawMarkerElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawMeasureElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawObjectElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawObjectOleElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawOpacityElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPageThumbnailElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawParamElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPathElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPluginElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPolygonElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawPolylineElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawRectElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawRegularPolygonElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawStrokeDashElement;
import org.odftoolkit.odfdom.dom.element.draw.DrawTextBoxElement;
import org.odftoolkit.odfdom.dom.element.form.FormButtonElement;
import org.odftoolkit.odfdom.dom.element.form.FormCheckboxElement;
import org.odftoolkit.odfdom.dom.element.form.FormColumnElement;
import org.odftoolkit.odfdom.dom.element.form.FormComboboxElement;
import org.odftoolkit.odfdom.dom.element.form.FormConnectionResourceElement;
import org.odftoolkit.odfdom.dom.element.form.FormDateElement;
import org.odftoolkit.odfdom.dom.element.form.FormFileElement;
import org.odftoolkit.odfdom.dom.element.form.FormFixedTextElement;
import org.odftoolkit.odfdom.dom.element.form.FormFormElement;
import org.odftoolkit.odfdom.dom.element.form.FormFormattedTextElement;
import org.odftoolkit.odfdom.dom.element.form.FormFrameElement;
import org.odftoolkit.odfdom.dom.element.form.FormGenericControlElement;
import org.odftoolkit.odfdom.dom.element.form.FormGridElement;
import org.odftoolkit.odfdom.dom.element.form.FormHiddenElement;
import org.odftoolkit.odfdom.dom.element.form.FormImageElement;
import org.odftoolkit.odfdom.dom.element.form.FormImageFrameElement;
import org.odftoolkit.odfdom.dom.element.form.FormItemElement;
import org.odftoolkit.odfdom.dom.element.form.FormListPropertyElement;
import org.odftoolkit.odfdom.dom.element.form.FormListValueElement;
import org.odftoolkit.odfdom.dom.element.form.FormListboxElement;
import org.odftoolkit.odfdom.dom.element.form.FormNumberElement;
import org.odftoolkit.odfdom.dom.element.form.FormOptionElement;
import org.odftoolkit.odfdom.dom.element.form.FormPasswordElement;
import org.odftoolkit.odfdom.dom.element.form.FormPropertiesElement;
import org.odftoolkit.odfdom.dom.element.form.FormPropertyElement;
import org.odftoolkit.odfdom.dom.element.form.FormRadioElement;
import org.odftoolkit.odfdom.dom.element.form.FormTextElement;
import org.odftoolkit.odfdom.dom.element.form.FormTextareaElement;
import org.odftoolkit.odfdom.dom.element.form.FormTimeElement;
import org.odftoolkit.odfdom.dom.element.form.FormValueRangeElement;
import org.odftoolkit.odfdom.dom.element.math.MathMathElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaAutoReloadElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaCreationDateElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaDateStringElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaDocumentStatisticElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaEditingCyclesElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaEditingDurationElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaGeneratorElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaHyperlinkBehaviourElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaInitialCreatorElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaKeywordElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaPrintDateElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaPrintedByElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaTemplateElement;
import org.odftoolkit.odfdom.dom.element.meta.MetaUserDefinedElement;
import org.odftoolkit.odfdom.dom.element.number.NumberAmPmElement;
import org.odftoolkit.odfdom.dom.element.number.NumberBooleanElement;
import org.odftoolkit.odfdom.dom.element.number.NumberBooleanStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberCurrencyStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberCurrencySymbolElement;
import org.odftoolkit.odfdom.dom.element.number.NumberDateStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberDayElement;
import org.odftoolkit.odfdom.dom.element.number.NumberDayOfWeekElement;
import org.odftoolkit.odfdom.dom.element.number.NumberEmbeddedTextElement;
import org.odftoolkit.odfdom.dom.element.number.NumberEraElement;
import org.odftoolkit.odfdom.dom.element.number.NumberFractionElement;
import org.odftoolkit.odfdom.dom.element.number.NumberHoursElement;
import org.odftoolkit.odfdom.dom.element.number.NumberMinutesElement;
import org.odftoolkit.odfdom.dom.element.number.NumberMonthElement;
import org.odftoolkit.odfdom.dom.element.number.NumberNumberElement;
import org.odftoolkit.odfdom.dom.element.number.NumberNumberStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberPercentageStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberQuarterElement;
import org.odftoolkit.odfdom.dom.element.number.NumberScientificNumberElement;
import org.odftoolkit.odfdom.dom.element.number.NumberSecondsElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTextContentElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTextElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTextStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberTimeStyleElement;
import org.odftoolkit.odfdom.dom.element.number.NumberWeekOfYearElement;
import org.odftoolkit.odfdom.dom.element.number.NumberYearElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeAnnotationEndElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeAutomaticStylesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeBinaryDataElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeBodyElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeChangeInfoElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeChartElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDatabaseElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDdeSourceElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentContentElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentMetaElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentSettingsElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDocumentStylesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeDrawingElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeEventListenersElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeFontFaceDeclsElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeFormsElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeImageElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeMasterStylesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeMetaElement;
import org.odftoolkit.odfdom.dom.element.office.OfficePresentationElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeScriptElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeScriptsElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeSettingsElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeSpreadsheetElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeStylesElement;
import org.odftoolkit.odfdom.dom.element.office.OfficeTextElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationAnimationGroupElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationAnimationsElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationDateTimeDeclElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationDateTimeElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationDimElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationEventListenerElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationFooterDeclElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationFooterElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationHeaderDeclElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationHeaderElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationHideShapeElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationHideTextElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationNotesElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationPlaceholderElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationPlayElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationSettingsElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationShowElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationShowShapeElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationShowTextElement;
import org.odftoolkit.odfdom.dom.element.presentation.PresentationSoundElement;
import org.odftoolkit.odfdom.dom.element.script.ScriptEventListenerElement;
import org.odftoolkit.odfdom.dom.element.style.StyleBackgroundImageElement;
import org.odftoolkit.odfdom.dom.element.style.StyleChartPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleColumnElement;
import org.odftoolkit.odfdom.dom.element.style.StyleColumnSepElement;
import org.odftoolkit.odfdom.dom.element.style.StyleColumnsElement;
import org.odftoolkit.odfdom.dom.element.style.StyleDefaultPageLayoutElement;
import org.odftoolkit.odfdom.dom.element.style.StyleDefaultStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleDrawingPagePropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleDropCapElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFontFaceElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterLeftElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFooterStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleFootnoteSepElement;
import org.odftoolkit.odfdom.dom.element.style.StyleGraphicPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHandoutMasterElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderFooterPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderLeftElement;
import org.odftoolkit.odfdom.dom.element.style.StyleHeaderStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleListLevelLabelAlignmentElement;
import org.odftoolkit.odfdom.dom.element.style.StyleListLevelPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMapElement;
import org.odftoolkit.odfdom.dom.element.style.StyleMasterPageElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutElement;
import org.odftoolkit.odfdom.dom.element.style.StylePageLayoutPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleParagraphPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StylePresentationPageLayoutElement;
import org.odftoolkit.odfdom.dom.element.style.StyleRegionCenterElement;
import org.odftoolkit.odfdom.dom.element.style.StyleRegionLeftElement;
import org.odftoolkit.odfdom.dom.element.style.StyleRegionRightElement;
import org.odftoolkit.odfdom.dom.element.style.StyleRubyPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleSectionPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleStyleElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTabStopElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTabStopsElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableCellPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableColumnPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTablePropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTableRowPropertiesElement;
import org.odftoolkit.odfdom.dom.element.style.StyleTextPropertiesElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgDefinitionSrcElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgDescElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgFontFaceFormatElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgFontFaceNameElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgFontFaceSrcElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgFontFaceUriElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgLinearGradientElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgRadialGradientElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgStopElement;
import org.odftoolkit.odfdom.dom.element.svg.SvgTitleElement;
import org.odftoolkit.odfdom.dom.element.table.TableBackgroundElement;
import org.odftoolkit.odfdom.dom.element.table.TableBodyElement;
import org.odftoolkit.odfdom.dom.element.table.TableCalculationSettingsElement;
import org.odftoolkit.odfdom.dom.element.table.TableCellAddressElement;
import org.odftoolkit.odfdom.dom.element.table.TableCellContentChangeElement;
import org.odftoolkit.odfdom.dom.element.table.TableCellContentDeletionElement;
import org.odftoolkit.odfdom.dom.element.table.TableCellRangeSourceElement;
import org.odftoolkit.odfdom.dom.element.table.TableChangeDeletionElement;
import org.odftoolkit.odfdom.dom.element.table.TableChangeTrackTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableConsolidationElement;
import org.odftoolkit.odfdom.dom.element.table.TableContentValidationElement;
import org.odftoolkit.odfdom.dom.element.table.TableContentValidationsElement;
import org.odftoolkit.odfdom.dom.element.table.TableCoveredTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableCutOffsElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotDisplayInfoElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotFieldElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotFieldReferenceElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotGroupMemberElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotGroupsElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotLayoutInfoElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotLevelElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotMemberElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotMembersElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotSortInfoElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotSubtotalElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotSubtotalsElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableDataPilotTablesElement;
import org.odftoolkit.odfdom.dom.element.table.TableDatabaseRangeElement;
import org.odftoolkit.odfdom.dom.element.table.TableDatabaseRangesElement;
import org.odftoolkit.odfdom.dom.element.table.TableDatabaseSourceQueryElement;
import org.odftoolkit.odfdom.dom.element.table.TableDatabaseSourceSqlElement;
import org.odftoolkit.odfdom.dom.element.table.TableDatabaseSourceTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableDdeLinkElement;
import org.odftoolkit.odfdom.dom.element.table.TableDdeLinksElement;
import org.odftoolkit.odfdom.dom.element.table.TableDeletionElement;
import org.odftoolkit.odfdom.dom.element.table.TableDeletionsElement;
import org.odftoolkit.odfdom.dom.element.table.TableDependenciesElement;
import org.odftoolkit.odfdom.dom.element.table.TableDependencyElement;
import org.odftoolkit.odfdom.dom.element.table.TableDescElement;
import org.odftoolkit.odfdom.dom.element.table.TableDetectiveElement;
import org.odftoolkit.odfdom.dom.element.table.TableErrorMacroElement;
import org.odftoolkit.odfdom.dom.element.table.TableErrorMessageElement;
import org.odftoolkit.odfdom.dom.element.table.TableEvenColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableEvenRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableFilterAndElement;
import org.odftoolkit.odfdom.dom.element.table.TableFilterConditionElement;
import org.odftoolkit.odfdom.dom.element.table.TableFilterElement;
import org.odftoolkit.odfdom.dom.element.table.TableFilterOrElement;
import org.odftoolkit.odfdom.dom.element.table.TableFilterSetItemElement;
import org.odftoolkit.odfdom.dom.element.table.TableFirstColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableFirstRowElement;
import org.odftoolkit.odfdom.dom.element.table.TableHelpMessageElement;
import org.odftoolkit.odfdom.dom.element.table.TableHighlightedRangeElement;
import org.odftoolkit.odfdom.dom.element.table.TableInsertionCutOffElement;
import org.odftoolkit.odfdom.dom.element.table.TableInsertionElement;
import org.odftoolkit.odfdom.dom.element.table.TableIterationElement;
import org.odftoolkit.odfdom.dom.element.table.TableLabelRangeElement;
import org.odftoolkit.odfdom.dom.element.table.TableLabelRangesElement;
import org.odftoolkit.odfdom.dom.element.table.TableLastColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableLastRowElement;
import org.odftoolkit.odfdom.dom.element.table.TableMovementCutOffElement;
import org.odftoolkit.odfdom.dom.element.table.TableMovementElement;
import org.odftoolkit.odfdom.dom.element.table.TableNamedExpressionElement;
import org.odftoolkit.odfdom.dom.element.table.TableNamedExpressionsElement;
import org.odftoolkit.odfdom.dom.element.table.TableNamedRangeElement;
import org.odftoolkit.odfdom.dom.element.table.TableNullDateElement;
import org.odftoolkit.odfdom.dom.element.table.TableOddColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableOddRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableOperationElement;
import org.odftoolkit.odfdom.dom.element.table.TablePreviousElement;
import org.odftoolkit.odfdom.dom.element.table.TableScenarioElement;
import org.odftoolkit.odfdom.dom.element.table.TableShapesElement;
import org.odftoolkit.odfdom.dom.element.table.TableSortByElement;
import org.odftoolkit.odfdom.dom.element.table.TableSortElement;
import org.odftoolkit.odfdom.dom.element.table.TableSortGroupsElement;
import org.odftoolkit.odfdom.dom.element.table.TableSourceCellRangeElement;
import org.odftoolkit.odfdom.dom.element.table.TableSourceRangeAddressElement;
import org.odftoolkit.odfdom.dom.element.table.TableSourceServiceElement;
import org.odftoolkit.odfdom.dom.element.table.TableSubtotalFieldElement;
import org.odftoolkit.odfdom.dom.element.table.TableSubtotalRuleElement;
import org.odftoolkit.odfdom.dom.element.table.TableSubtotalRulesElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableCellElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderColumnsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableHeaderRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowGroupElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableRowsElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableSourceElement;
import org.odftoolkit.odfdom.dom.element.table.TableTableTemplateElement;
import org.odftoolkit.odfdom.dom.element.table.TableTargetRangeAddressElement;
import org.odftoolkit.odfdom.dom.element.table.TableTitleElement;
import org.odftoolkit.odfdom.dom.element.table.TableTrackedChangesElement;
import org.odftoolkit.odfdom.dom.element.text.*;
import org.odftoolkit.odfdom.dom.element.xforms.XformsModelElement;
import org.odftoolkit.odfdom.pkg.OdfElement;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class PrintElementVisitor extends DefaultElementVisitor {

	@Override
	public void visit(OdfElement element) {
		System.out.println("(abstract) OdfElement " + element);
	}


	@Override
	public void visit(DrawPageElement ele) {
		System.out.println("DrawPageElement " + ele);
		// OdfElement child = OdfElement.findFirstChildNode(OdfElement.class, ele);
		if (ele.hasChildNodes()) {
			final NodeList childLs = ele.getChildNodes();
			final int childLen = childLs.getLength();
			for(int i = 0; i < childLen; i++) {
				Node child = childLs.item(i);
				if (child instanceof OdfElement) {
					OdfElement childElt = (OdfElement) child;
					childElt.accept(this);
				}
			}
		}		
	}

	
	
	
	@Override
	public void visit(AnimAnimateElement ele) {
		System.out.println("AnimAnimateElement " + ele);
	}

	@Override
	public void visit(AnimAnimateColorElement ele) {
		System.out.println("AnimAnimateColorElement " + ele);

	}

	@Override
	public void visit(AnimAnimateMotionElement ele) {
		System.out.println("AnimAnimateMotionElement " + ele);

	}

	@Override
	public void visit(AnimAnimateTransformElement ele) {
		System.out.println("AnimAnimateTransformElement " + ele);

	}

	@Override
	public void visit(AnimAudioElement ele) {
		System.out.println("AnimAudioElement " + ele);

	}

	@Override
	public void visit(AnimCommandElement ele) {
		System.out.println("AnimCommandElement " + ele);

	}

	@Override
	public void visit(AnimIterateElement ele) {
		System.out.println("AnimIterateElement " + ele);

	}

	@Override
	public void visit(AnimParElement ele) {
		System.out.println("AnimParElement " + ele);

	}

	@Override
	public void visit(AnimParamElement ele) {
		System.out.println("AnimParamElement " + ele);

	}

	@Override
	public void visit(AnimSeqElement ele) {
		System.out.println("AnimSeqElement " + ele);

	}

	@Override
	public void visit(AnimSetElement ele) {
		System.out.println("AnimSetElement " + ele);

	}

	@Override
	public void visit(AnimTransitionFilterElement ele) {
		System.out.println("AnimTransitionFilterElement " + ele);

	}

	@Override
	public void visit(ChartAxisElement ele) {
		System.out.println("ChartAxisElement " + ele);

	}

	@Override
	public void visit(ChartCategoriesElement ele) {
		System.out.println("ChartCategoriesElement " + ele);

	}

	@Override
	public void visit(ChartChartElement ele) {
		System.out.println("ChartChartElement " + ele);

	}

	@Override
	public void visit(ChartDataLabelElement ele) {
		System.out.println("ChartDataLabelElement " + ele);

	}

	@Override
	public void visit(ChartDataPointElement ele) {
		System.out.println("ChartDataPointElement " + ele);

	}

	@Override
	public void visit(ChartDomainElement ele) {
		System.out.println("ChartDomainElement " + ele);

	}

	@Override
	public void visit(ChartEquationElement ele) {
		System.out.println("ChartEquationElement " + ele);

	}

	@Override
	public void visit(ChartErrorIndicatorElement ele) {
		System.out.println("ChartErrorIndicatorElement " + ele);

	}

	@Override
	public void visit(ChartFloorElement ele) {
		System.out.println("ChartFloorElement " + ele);

	}

	@Override
	public void visit(ChartFooterElement ele) {
		System.out.println("ChartFooterElement " + ele);

	}

	@Override
	public void visit(ChartGridElement ele) {
		System.out.println("ChartGridElement " + ele);

	}

	@Override
	public void visit(ChartLabelSeparatorElement ele) {
		System.out.println("ChartLabelSeparatorElement " + ele);

	}

	@Override
	public void visit(ChartLegendElement ele) {
		System.out.println("ChartLegendElement " + ele);

	}

	@Override
	public void visit(ChartMeanValueElement ele) {
		System.out.println("ChartMeanValueElement " + ele);

	}

	@Override
	public void visit(ChartPlotAreaElement ele) {
		System.out.println("ChartPlotAreaElement " + ele);

	}

	@Override
	public void visit(ChartRegressionCurveElement ele) {
		System.out.println("ChartRegressionCurveElement " + ele);

	}

	@Override
	public void visit(ChartSeriesElement ele) {
		System.out.println("ChartSeriesElement " + ele);

	}

	@Override
	public void visit(ChartStockGainMarkerElement ele) {
		System.out.println("ChartStockGainMarkerElement " + ele);

	}

	@Override
	public void visit(ChartStockLossMarkerElement ele) {
		System.out.println("ChartStockLossMarkerElement " + ele);

	}

	@Override
	public void visit(ChartStockRangeLineElement ele) {
		System.out.println("ChartStockRangeLineElement " + ele);

	}

	@Override
	public void visit(ChartSubtitleElement ele) {
		System.out.println("ChartSubtitleElement " + ele);

	}

	@Override
	public void visit(ChartSymbolImageElement ele) {
		System.out.println("ChartSymbolImageElement " + ele);

	}

	@Override
	public void visit(ChartTitleElement ele) {
		System.out.println("ChartTitleElement " + ele);

	}

	@Override
	public void visit(ChartWallElement ele) {
		System.out.println("ChartWallElement " + ele);

	}

	@Override
	public void visit(ConfigConfigItemElement ele) {
		System.out.println("ConfigConfigItemElement " + ele);

	}

	@Override
	public void visit(ConfigConfigItemMapEntryElement ele) {
		System.out.println("ConfigConfigItemMapEntryElement " + ele);

	}

	@Override
	public void visit(ConfigConfigItemMapIndexedElement ele) {
		System.out.println("ConfigConfigItemMapIndexedElement " + ele);

	}

	@Override
	public void visit(ConfigConfigItemMapNamedElement ele) {
		System.out.println("ConfigConfigItemMapNamedElement " + ele);

	}

	@Override
	public void visit(ConfigConfigItemSetElement ele) {
		System.out.println("ConfigConfigItemSetElement " + ele);

	}

	@Override
	public void visit(DbApplicationConnectionSettingsElement ele) {
		System.out.println("DbApplicationConnectionSettingsElement " + ele);

	}

	@Override
	public void visit(DbAutoIncrementElement ele) {
		System.out.println("DbAutoIncrementElement " + ele);

	}

	@Override
	public void visit(DbCharacterSetElement ele) {
		System.out.println("DbCharacterSetElement " + ele);

	}

	@Override
	public void visit(DbColumnElement ele) {
		System.out.println("DbColumnElement " + ele);

	}

	@Override
	public void visit(DbColumnDefinitionElement ele) {
		System.out.println("DbColumnDefinitionElement " + ele);

	}

	@Override
	public void visit(DbColumnDefinitionsElement ele) {
		System.out.println("DbColumnDefinitionsElement " + ele);

	}

	@Override
	public void visit(DbColumnsElement ele) {
		System.out.println("DbColumnsElement " + ele);

	}

	@Override
	public void visit(DbComponentElement ele) {
		System.out.println("DbComponentElement " + ele);

	}

	@Override
	public void visit(DbComponentCollectionElement ele) {
		System.out.println("DbComponentCollectionElement " + ele);

	}

	@Override
	public void visit(DbConnectionDataElement ele) {
		System.out.println("DbConnectionDataElement " + ele);

	}

	@Override
	public void visit(DbConnectionResourceElement ele) {
		System.out.println("DbConnectionResourceElement " + ele);

	}

	@Override
	public void visit(DbDataSourceElement ele) {
		System.out.println("DbDataSourceElement " + ele);

	}

	@Override
	public void visit(DbDataSourceSettingElement ele) {
		System.out.println("DbDataSourceSettingElement " + ele);

	}

	@Override
	public void visit(DbDataSourceSettingValueElement ele) {
		System.out.println("DbDataSourceSettingValueElement " + ele);

	}

	@Override
	public void visit(DbDataSourceSettingsElement ele) {
		System.out.println("DbDataSourceSettingsElement " + ele);

	}

	@Override
	public void visit(DbDatabaseDescriptionElement ele) {
		System.out.println("DbDatabaseDescriptionElement " + ele);

	}

	@Override
	public void visit(DbDelimiterElement ele) {
		System.out.println("DbDelimiterElement " + ele);

	}

	@Override
	public void visit(DbDriverSettingsElement ele) {
		System.out.println("DbDriverSettingsElement " + ele);

	}

	@Override
	public void visit(DbFileBasedDatabaseElement ele) {
		System.out.println("DbFileBasedDatabaseElement " + ele);

	}

	@Override
	public void visit(DbFilterStatementElement ele) {
		System.out.println("DbFilterStatementElement " + ele);

	}

	@Override
	public void visit(DbFormsElement ele) {
		System.out.println("DbFormsElement " + ele);

	}

	@Override
	public void visit(DbIndexElement ele) {
		System.out.println("DbIndexElement " + ele);

	}

	@Override
	public void visit(DbIndexColumnElement ele) {
		System.out.println("DbIndexColumnElement " + ele);

	}

	@Override
	public void visit(DbIndexColumnsElement ele) {
		System.out.println("DbIndexColumnsElement " + ele);

	}

	@Override
	public void visit(DbIndicesElement ele) {
		System.out.println("DbIndicesElement " + ele);

	}

	@Override
	public void visit(DbKeyElement ele) {
		System.out.println("DbKeyElement " + ele);

	}

	@Override
	public void visit(DbKeyColumnElement ele) {
		System.out.println("DbKeyColumnElement " + ele);

	}

	@Override
	public void visit(DbKeyColumnsElement ele) {
		System.out.println("DbKeyColumnsElement " + ele);

	}

	@Override
	public void visit(DbKeysElement ele) {
		System.out.println("DbKeysElement " + ele);

	}

	@Override
	public void visit(DbLoginElement ele) {
		System.out.println("DbLoginElement " + ele);

	}

	@Override
	public void visit(DbOrderStatementElement ele) {
		System.out.println("DbOrderStatementElement " + ele);

	}

	@Override
	public void visit(DbQueriesElement ele) {
		System.out.println("DbQueriesElement " + ele);

	}

	@Override
	public void visit(DbQueryElement ele) {
		System.out.println("DbQueryElement " + ele);

	}

	@Override
	public void visit(DbQueryCollectionElement ele) {
		System.out.println("DbQueryCollectionElement " + ele);

	}

	@Override
	public void visit(DbReportsElement ele) {
		System.out.println("DbReportsElement " + ele);

	}

	@Override
	public void visit(DbSchemaDefinitionElement ele) {
		System.out.println("DbSchemaDefinitionElement " + ele);

	}

	@Override
	public void visit(DbServerDatabaseElement ele) {
		System.out.println("DbServerDatabaseElement " + ele);

	}

	@Override
	public void visit(DbTableDefinitionElement ele) {
		System.out.println("DbTableDefinitionElement " + ele);

	}

	@Override
	public void visit(DbTableDefinitionsElement ele) {
		System.out.println("DbTableDefinitionsElement " + ele);

	}

	@Override
	public void visit(DbTableExcludeFilterElement ele) {
		System.out.println("DbTableExcludeFilterElement " + ele);

	}

	@Override
	public void visit(DbTableFilterElement ele) {
		System.out.println("DbTableFilterElement " + ele);

	}

	@Override
	public void visit(DbTableFilterPatternElement ele) {
		System.out.println("DbTableFilterPatternElement " + ele);

	}

	@Override
	public void visit(DbTableIncludeFilterElement ele) {
		System.out.println("DbTableIncludeFilterElement " + ele);

	}

	@Override
	public void visit(DbTableRepresentationElement ele) {
		System.out.println("DbTableRepresentationElement " + ele);

	}

	@Override
	public void visit(DbTableRepresentationsElement ele) {
		System.out.println("DbTableRepresentationsElement " + ele);

	}

	@Override
	public void visit(DbTableSettingElement ele) {
		System.out.println("DbTableSettingElement " + ele);

	}

	@Override
	public void visit(DbTableSettingsElement ele) {
		System.out.println("DbTableSettingsElement " + ele);

	}

	@Override
	public void visit(DbTableTypeElement ele) {
		System.out.println("DbTableTypeElement " + ele);

	}

	@Override
	public void visit(DbTableTypeFilterElement ele) {
		System.out.println("DbTableTypeFilterElement " + ele);

	}

	@Override
	public void visit(DbUpdateTableElement ele) {
		System.out.println("DbUpdateTableElement " + ele);

	}

	@Override
	public void visit(DcCreatorElement ele) {
		System.out.println("DcCreatorElement " + ele);

	}

	@Override
	public void visit(DcDateElement ele) {
		System.out.println("DcDateElement " + ele);

	}

	@Override
	public void visit(DcDescriptionElement ele) {
		System.out.println("DcDescriptionElement " + ele);

	}

	@Override
	public void visit(DcLanguageElement ele) {
		System.out.println("DcLanguageElement " + ele);

	}

	@Override
	public void visit(DcSubjectElement ele) {
		System.out.println("DcSubjectElement " + ele);

	}

	@Override
	public void visit(DcTitleElement ele) {
		System.out.println("DcTitleElement " + ele);

	}

	@Override
	public void visit(Dr3dCubeElement ele) {
		System.out.println("Dr3dCubeElement " + ele);

	}

	@Override
	public void visit(Dr3dExtrudeElement ele) {
		System.out.println("Dr3dExtrudeElement " + ele);

	}

	@Override
	public void visit(Dr3dLightElement ele) {
		System.out.println("Dr3dLightElement " + ele);

	}

	@Override
	public void visit(Dr3dRotateElement ele) {
		System.out.println("Dr3dRotateElement " + ele);

	}

	@Override
	public void visit(Dr3dSceneElement ele) {
		System.out.println("Dr3dSceneElement " + ele);

	}

	@Override
	public void visit(Dr3dSphereElement ele) {
		System.out.println("Dr3dSphereElement " + ele);

	}

	@Override
	public void visit(DrawAElement ele) {
		System.out.println("DrawAElement " + ele);

	}

	@Override
	public void visit(DrawAppletElement ele) {
		System.out.println("DrawAppletElement " + ele);

	}

	@Override
	public void visit(DrawAreaCircleElement ele) {
		System.out.println("DrawAreaCircleElement " + ele);

	}

	@Override
	public void visit(DrawAreaPolygonElement ele) {
		System.out.println("DrawAreaPolygonElement " + ele);

	}

	@Override
	public void visit(DrawAreaRectangleElement ele) {
		System.out.println("DrawAreaRectangleElement " + ele);

	}

	@Override
	public void visit(DrawCaptionElement ele) {
		System.out.println("DrawCaptionElement " + ele);

	}

	@Override
	public void visit(DrawCircleElement ele) {
		System.out.println("DrawCircleElement " + ele);

	}

	@Override
	public void visit(DrawConnectorElement ele) {
		System.out.println("DrawConnectorElement " + ele);

	}

	@Override
	public void visit(DrawContourPathElement ele) {
		System.out.println("DrawContourPathElement " + ele);

	}

	@Override
	public void visit(DrawContourPolygonElement ele) {
		System.out.println("DrawContourPolygonElement " + ele);

	}

	@Override
	public void visit(DrawControlElement ele) {
		System.out.println("DrawControlElement " + ele);

	}

	@Override
	public void visit(DrawCustomShapeElement ele) {
		System.out.println("DrawCustomShapeElement " + ele);

	}

	@Override
	public void visit(DrawEllipseElement ele) {
		System.out.println("DrawEllipseElement " + ele);

	}

	@Override
	public void visit(DrawEnhancedGeometryElement ele) {
		System.out.println("DrawEnhancedGeometryElement " + ele);

	}

	@Override
	public void visit(DrawEquationElement ele) {
		System.out.println("DrawEquationElement " + ele);

	}

	@Override
	public void visit(DrawFillImageElement ele) {
		System.out.println("DrawFillImageElement " + ele);

	}

	@Override
	public void visit(DrawFloatingFrameElement ele) {
		System.out.println("DrawFloatingFrameElement " + ele);

	}

	@Override
	public void visit(DrawFrameElement ele) {
		System.out.println("DrawFrameElement " + ele);

	}

	@Override
	public void visit(DrawGElement ele) {
		System.out.println("DrawGElement " + ele);

	}

	@Override
	public void visit(DrawGluePointElement ele) {
		System.out.println("DrawGluePointElement " + ele);

	}

	@Override
	public void visit(DrawGradientElement ele) {
		System.out.println("DrawGradientElement " + ele);

	}

	@Override
	public void visit(DrawHandleElement ele) {
		System.out.println("DrawHandleElement " + ele);

	}

	@Override
	public void visit(DrawHatchElement ele) {
		System.out.println("DrawHatchElement " + ele);

	}

	@Override
	public void visit(DrawImageElement ele) {
		System.out.println("DrawImageElement " + ele);

	}

	@Override
	public void visit(DrawImageMapElement ele) {
		System.out.println("DrawImageMapElement " + ele);

	}

	@Override
	public void visit(DrawLayerElement ele) {
		System.out.println("DrawLayerElement " + ele);

	}

	@Override
	public void visit(DrawLayerSetElement ele) {
		System.out.println("DrawLayerSetElement " + ele);

	}

	@Override
	public void visit(DrawLineElement ele) {
		System.out.println("DrawLineElement " + ele);

	}

	@Override
	public void visit(DrawMarkerElement ele) {
		System.out.println("DrawMarkerElement " + ele);

	}

	@Override
	public void visit(DrawMeasureElement ele) {
		System.out.println("DrawMeasureElement " + ele);

	}

	@Override
	public void visit(DrawObjectElement ele) {
		System.out.println("DrawObjectElement " + ele);

	}

	@Override
	public void visit(DrawObjectOleElement ele) {
		System.out.println("DrawObjectOleElement " + ele);

	}

	@Override
	public void visit(DrawOpacityElement ele) {
		System.out.println("DrawOpacityElement " + ele);

	}

	@Override
	public void visit(DrawPageThumbnailElement ele) {
		System.out.println("DrawPageThumbnailElement " + ele);

	}

	@Override
	public void visit(DrawParamElement ele) {
		System.out.println("DrawParamElement " + ele);

	}

	@Override
	public void visit(DrawPathElement ele) {
		System.out.println("DrawPathElement " + ele);

	}

	@Override
	public void visit(DrawPluginElement ele) {
		System.out.println("DrawPluginElement " + ele);

	}

	@Override
	public void visit(DrawPolygonElement ele) {
		System.out.println("DrawPolygonElement " + ele);

	}

	@Override
	public void visit(DrawPolylineElement ele) {
		System.out.println("DrawPolylineElement " + ele);

	}

	@Override
	public void visit(DrawRectElement ele) {
		System.out.println("DrawRectElement " + ele);

	}

	@Override
	public void visit(DrawRegularPolygonElement ele) {
		System.out.println("DrawRegularPolygonElement " + ele);

	}

	@Override
	public void visit(DrawStrokeDashElement ele) {
		System.out.println("DrawStrokeDashElement " + ele);

	}

	@Override
	public void visit(DrawTextBoxElement ele) {
		System.out.println("DrawTextBoxElement " + ele);

	}

	@Override
	public void visit(FormButtonElement ele) {
		System.out.println("FormButtonElement " + ele);

	}

	@Override
	public void visit(FormCheckboxElement ele) {
		System.out.println("FormCheckboxElement " + ele);

	}

	@Override
	public void visit(FormColumnElement ele) {
		System.out.println("FormColumnElement " + ele);

	}

	@Override
	public void visit(FormComboboxElement ele) {
		System.out.println("FormComboboxElement " + ele);

	}

	@Override
	public void visit(FormConnectionResourceElement ele) {
		System.out.println("FormConnectionResourceElement " + ele);

	}

	@Override
	public void visit(FormDateElement ele) {
		System.out.println("FormDateElement " + ele);

	}

	@Override
	public void visit(FormFileElement ele) {
		System.out.println("FormFileElement " + ele);

	}

	@Override
	public void visit(FormFixedTextElement ele) {
		System.out.println("FormFixedTextElement " + ele);

	}

	@Override
	public void visit(FormFormElement ele) {
		System.out.println("FormFormElement " + ele);

	}

	@Override
	public void visit(FormFormattedTextElement ele) {
		System.out.println("FormFormattedTextElement " + ele);

	}

	@Override
	public void visit(FormFrameElement ele) {
		System.out.println("FormFrameElement " + ele);

	}

	@Override
	public void visit(FormGenericControlElement ele) {
		System.out.println("FormGenericControlElement " + ele);

	}

	@Override
	public void visit(FormGridElement ele) {
		System.out.println("FormGridElement " + ele);

	}

	@Override
	public void visit(FormHiddenElement ele) {
		System.out.println("FormHiddenElement " + ele);

	}

	@Override
	public void visit(FormImageElement ele) {
		System.out.println("FormImageElement " + ele);

	}

	@Override
	public void visit(FormImageFrameElement ele) {
		System.out.println("FormImageFrameElement " + ele);

	}

	@Override
	public void visit(FormItemElement ele) {
		System.out.println("FormItemElement " + ele);

	}

	@Override
	public void visit(FormListPropertyElement ele) {
		System.out.println("FormListPropertyElement " + ele);

	}

	@Override
	public void visit(FormListValueElement ele) {
		System.out.println("FormListValueElement " + ele);

	}

	@Override
	public void visit(FormListboxElement ele) {
		System.out.println("FormListboxElement " + ele);

	}

	@Override
	public void visit(FormNumberElement ele) {
		System.out.println("FormNumberElement " + ele);

	}

	@Override
	public void visit(FormOptionElement ele) {
		System.out.println("FormOptionElement " + ele);

	}

	@Override
	public void visit(FormPasswordElement ele) {
		System.out.println("FormPasswordElement " + ele);

	}

	@Override
	public void visit(FormPropertiesElement ele) {
		System.out.println("FormPropertiesElement " + ele);

	}

	@Override
	public void visit(FormPropertyElement ele) {
		System.out.println("FormPropertyElement " + ele);

	}

	@Override
	public void visit(FormRadioElement ele) {
		System.out.println("FormRadioElement " + ele);

	}

	@Override
	public void visit(FormTextElement ele) {
		System.out.println("FormTextElement " + ele);

	}

	@Override
	public void visit(FormTextareaElement ele) {
		System.out.println("FormTextareaElement " + ele);

	}

	@Override
	public void visit(FormTimeElement ele) {
		System.out.println("FormTimeElement " + ele);

	}

	@Override
	public void visit(FormValueRangeElement ele) {
		System.out.println("FormValueRangeElement " + ele);

	}

	@Override
	public void visit(MathMathElement ele) {
		System.out.println("MathMathElement " + ele);

	}

	@Override
	public void visit(MetaAutoReloadElement ele) {
		System.out.println("MetaAutoReloadElement " + ele);

	}

	@Override
	public void visit(MetaCreationDateElement ele) {
		System.out.println("MetaCreationDateElement " + ele);

	}

	@Override
	public void visit(MetaDateStringElement ele) {
		System.out.println("MetaDateStringElement " + ele);

	}

	@Override
	public void visit(MetaDocumentStatisticElement ele) {
		System.out.println("MetaDocumentStatisticElement " + ele);

	}

	@Override
	public void visit(MetaEditingCyclesElement ele) {
		System.out.println("MetaEditingCyclesElement " + ele);

	}

	@Override
	public void visit(MetaEditingDurationElement ele) {
		System.out.println("MetaEditingDurationElement " + ele);

	}

	@Override
	public void visit(MetaGeneratorElement ele) {
		System.out.println("MetaGeneratorElement " + ele);

	}

	@Override
	public void visit(MetaHyperlinkBehaviourElement ele) {
		System.out.println("MetaHyperlinkBehaviourElement " + ele);

	}

	@Override
	public void visit(MetaInitialCreatorElement ele) {
		System.out.println("MetaInitialCreatorElement " + ele);

	}

	@Override
	public void visit(MetaKeywordElement ele) {
		System.out.println("MetaKeywordElement " + ele);

	}

	@Override
	public void visit(MetaPrintDateElement ele) {
		System.out.println("MetaPrintDateElement " + ele);

	}

	@Override
	public void visit(MetaPrintedByElement ele) {
		System.out.println("MetaPrintedByElement " + ele);

	}

	@Override
	public void visit(MetaTemplateElement ele) {
		System.out.println("MetaTemplateElement " + ele);

	}

	@Override
	public void visit(MetaUserDefinedElement ele) {
		System.out.println("MetaUserDefinedElement " + ele);

	}

	@Override
	public void visit(NumberAmPmElement ele) {
		System.out.println("NumberAmPmElement " + ele);

	}

	@Override
	public void visit(NumberBooleanElement ele) {
		System.out.println("NumberBooleanElement " + ele);

	}

	@Override
	public void visit(NumberBooleanStyleElement ele) {
		System.out.println("NumberBooleanStyleElement " + ele);

	}

	@Override
	public void visit(NumberCurrencyStyleElement ele) {
		System.out.println("NumberCurrencyStyleElement " + ele);

	}

	@Override
	public void visit(NumberCurrencySymbolElement ele) {
		System.out.println("NumberCurrencySymbolElement " + ele);

	}

	@Override
	public void visit(NumberDateStyleElement ele) {
		System.out.println("NumberDateStyleElement " + ele);

	}

	@Override
	public void visit(NumberDayElement ele) {
		System.out.println("NumberDayElement " + ele);

	}

	@Override
	public void visit(NumberDayOfWeekElement ele) {
		System.out.println("NumberDayOfWeekElement " + ele);

	}

	@Override
	public void visit(NumberEmbeddedTextElement ele) {
		System.out.println("NumberEmbeddedTextElement " + ele);

	}

	@Override
	public void visit(NumberEraElement ele) {
		System.out.println("NumberEraElement " + ele);

	}

	@Override
	public void visit(NumberFractionElement ele) {
		System.out.println("NumberFractionElement " + ele);

	}

	@Override
	public void visit(NumberHoursElement ele) {
		System.out.println("NumberHoursElement " + ele);

	}

	@Override
	public void visit(NumberMinutesElement ele) {
		System.out.println("NumberMinutesElement " + ele);

	}

	@Override
	public void visit(NumberMonthElement ele) {
		System.out.println("NumberMonthElement " + ele);

	}

	@Override
	public void visit(NumberNumberElement ele) {
		System.out.println("NumberNumberElement " + ele);

	}

	@Override
	public void visit(NumberNumberStyleElement ele) {
		System.out.println("NumberNumberStyleElement " + ele);

	}

	@Override
	public void visit(NumberPercentageStyleElement ele) {
		System.out.println("NumberPercentageStyleElement " + ele);

	}

	@Override
	public void visit(NumberQuarterElement ele) {
		System.out.println("NumberQuarterElement " + ele);

	}

	@Override
	public void visit(NumberScientificNumberElement ele) {
		System.out.println("NumberScientificNumberElement " + ele);

	}

	@Override
	public void visit(NumberSecondsElement ele) {
		System.out.println("NumberSecondsElement " + ele);

	}

	@Override
	public void visit(NumberTextElement ele) {
		System.out.println("NumberTextElement " + ele);

	}

	@Override
	public void visit(NumberTextContentElement ele) {
		System.out.println("NumberTextContentElement " + ele);

	}

	@Override
	public void visit(NumberTextStyleElement ele) {
		System.out.println("NumberTextStyleElement " + ele);

	}

	@Override
	public void visit(NumberTimeStyleElement ele) {
		System.out.println("NumberTimeStyleElement " + ele);

	}

	@Override
	public void visit(NumberWeekOfYearElement ele) {
		System.out.println("NumberWeekOfYearElement " + ele);

	}

	@Override
	public void visit(NumberYearElement ele) {
		System.out.println("NumberYearElement " + ele);

	}

	@Override
	public void visit(OfficeAnnotationElement ele) {
		System.out.println("OfficeAnnotationElement " + ele);

	}

	@Override
	public void visit(OfficeAnnotationEndElement ele) {
		System.out.println("OfficeAnnotationEndElement " + ele);

	}

	@Override
	public void visit(OfficeAutomaticStylesElement ele) {
		System.out.println("OfficeAutomaticStylesElement " + ele);

	}

	@Override
	public void visit(OfficeBinaryDataElement ele) {
		System.out.println("OfficeBinaryDataElement " + ele);

	}

	@Override
	public void visit(OfficeBodyElement ele) {
		System.out.println("OfficeBodyElement " + ele);

	}

	@Override
	public void visit(OfficeChangeInfoElement ele) {
		System.out.println("OfficeChangeInfoElement " + ele);

	}

	@Override
	public void visit(OfficeChartElement ele) {
		System.out.println("OfficeChartElement " + ele);

	}

	@Override
	public void visit(OfficeDatabaseElement ele) {
		System.out.println("OfficeDatabaseElement " + ele);

	}

	@Override
	public void visit(OfficeDdeSourceElement ele) {
		System.out.println("OfficeDdeSourceElement " + ele);

	}

	@Override
	public void visit(OfficeDocumentElement ele) {
		System.out.println("OfficeDocumentElement " + ele);

	}

	@Override
	public void visit(OfficeDocumentContentElement ele) {
		System.out.println("OfficeDocumentContentElement " + ele);

	}

	@Override
	public void visit(OfficeDocumentMetaElement ele) {
		System.out.println("OfficeDocumentMetaElement " + ele);

	}

	@Override
	public void visit(OfficeDocumentSettingsElement ele) {
		System.out.println("OfficeDocumentSettingsElement " + ele);

	}

	@Override
	public void visit(OfficeDocumentStylesElement ele) {
		System.out.println("OfficeDocumentStylesElement " + ele);

	}

	@Override
	public void visit(OfficeDrawingElement ele) {
		System.out.println("OfficeDrawingElement " + ele);

	}

	@Override
	public void visit(OfficeEventListenersElement ele) {
		System.out.println("OfficeEventListenersElement " + ele);

	}

	@Override
	public void visit(OfficeFontFaceDeclsElement ele) {
		System.out.println("OfficeFontFaceDeclsElement " + ele);

	}

	@Override
	public void visit(OfficeFormsElement ele) {
		System.out.println("OfficeFormsElement " + ele);

	}

	@Override
	public void visit(OfficeImageElement ele) {
		System.out.println("OfficeImageElement " + ele);

	}

	@Override
	public void visit(OfficeMasterStylesElement ele) {
		System.out.println("OfficeMasterStylesElement " + ele);

	}

	@Override
	public void visit(OfficeMetaElement ele) {
		System.out.println("OfficeMetaElement " + ele);

	}

	@Override
	public void visit(OfficePresentationElement ele) {
		System.out.println("OfficePresentationElement " + ele);

	}

	@Override
	public void visit(OfficeScriptElement ele) {
		System.out.println("OfficeScriptElement " + ele);

	}

	@Override
	public void visit(OfficeScriptsElement ele) {
		System.out.println("OfficeScriptsElement " + ele);

	}

	@Override
	public void visit(OfficeSettingsElement ele) {
		System.out.println("OfficeSettingsElement " + ele);

	}

	@Override
	public void visit(OfficeSpreadsheetElement ele) {
		System.out.println("OfficeSpreadsheetElement " + ele);

	}

	@Override
	public void visit(OfficeStylesElement ele) {
		System.out.println("OfficeStylesElement " + ele);

	}

	@Override
	public void visit(OfficeTextElement ele) {
		System.out.println("OfficeTextElement " + ele);

	}

	@Override
	public void visit(PresentationAnimationGroupElement ele) {
		System.out.println("PresentationAnimationGroupElement " + ele);

	}

	@Override
	public void visit(PresentationAnimationsElement ele) {
		System.out.println("PresentationAnimationsElement " + ele);

	}

	@Override
	public void visit(PresentationDateTimeElement ele) {
		System.out.println("PresentationDateTimeElement " + ele);

	}

	@Override
	public void visit(PresentationDateTimeDeclElement ele) {
		System.out.println("PresentationDateTimeDeclElement " + ele);

	}

	@Override
	public void visit(PresentationDimElement ele) {
		System.out.println("PresentationDimElement " + ele);

	}

	@Override
	public void visit(PresentationEventListenerElement ele) {
		System.out.println("PresentationEventListenerElement " + ele);

	}

	@Override
	public void visit(PresentationFooterElement ele) {
		System.out.println("PresentationFooterElement " + ele);

	}

	@Override
	public void visit(PresentationFooterDeclElement ele) {
		System.out.println("PresentationFooterDeclElement " + ele);

	}

	@Override
	public void visit(PresentationHeaderElement ele) {
		System.out.println("PresentationHeaderElement " + ele);

	}

	@Override
	public void visit(PresentationHeaderDeclElement ele) {
		System.out.println("PresentationHeaderDeclElement " + ele);

	}

	@Override
	public void visit(PresentationHideShapeElement ele) {
		System.out.println("PresentationHideShapeElement " + ele);

	}

	@Override
	public void visit(PresentationHideTextElement ele) {
		System.out.println("PresentationHideTextElement " + ele);

	}

	@Override
	public void visit(PresentationNotesElement ele) {
		System.out.println("PresentationNotesElement " + ele);

	}

	@Override
	public void visit(PresentationPlaceholderElement ele) {
		System.out.println("PresentationPlaceholderElement " + ele);

	}

	@Override
	public void visit(PresentationPlayElement ele) {
		System.out.println("PresentationPlayElement " + ele);

	}

	@Override
	public void visit(PresentationSettingsElement ele) {
		System.out.println("PresentationSettingsElement " + ele);

	}

	@Override
	public void visit(PresentationShowElement ele) {
		System.out.println("PresentationShowElement " + ele);

	}

	@Override
	public void visit(PresentationShowShapeElement ele) {
		System.out.println("PresentationShowShapeElement " + ele);

	}

	@Override
	public void visit(PresentationShowTextElement ele) {
		System.out.println("PresentationShowTextElement " + ele);

	}

	@Override
	public void visit(PresentationSoundElement ele) {
		System.out.println("PresentationSoundElement " + ele);

	}

	@Override
	public void visit(ScriptEventListenerElement ele) {
		System.out.println("ScriptEventListenerElement " + ele);

	}

	@Override
	public void visit(StyleBackgroundImageElement ele) {
		System.out.println("StyleBackgroundImageElement " + ele);

	}

	@Override
	public void visit(StyleChartPropertiesElement ele) {
		System.out.println("StyleChartPropertiesElement " + ele);

	}

	@Override
	public void visit(StyleColumnElement ele) {
		System.out.println("StyleColumnElement " + ele);

	}

	@Override
	public void visit(StyleColumnSepElement ele) {
		System.out.println("StyleColumnSepElement " + ele);

	}

	@Override
	public void visit(StyleColumnsElement ele) {
		System.out.println("StyleColumnsElement " + ele);

	}

	@Override
	public void visit(StyleDefaultPageLayoutElement ele) {
		System.out.println("StyleDefaultPageLayoutElement " + ele);

	}

	@Override
	public void visit(StyleDefaultStyleElement ele) {
		System.out.println("StyleDefaultStyleElement " + ele);

	}

	@Override
	public void visit(StyleDrawingPagePropertiesElement ele) {
		System.out.println("StyleDrawingPagePropertiesElement " + ele);

	}

	@Override
	public void visit(StyleDropCapElement ele) {
		System.out.println("StyleDropCapElement " + ele);

	}

	@Override
	public void visit(StyleFontFaceElement ele) {
		System.out.println("StyleFontFaceElement " + ele);

	}

	@Override
	public void visit(StyleFooterElement ele) {
		System.out.println("StyleFooterElement " + ele);

	}

	@Override
	public void visit(StyleFooterLeftElement ele) {
		System.out.println("StyleFooterLeftElement " + ele);

	}

	@Override
	public void visit(StyleFooterStyleElement ele) {
		System.out.println("StyleFooterStyleElement " + ele);

	}

	@Override
	public void visit(StyleFootnoteSepElement ele) {
		System.out.println("StyleFootnoteSepElement " + ele);

	}

	@Override
	public void visit(StyleGraphicPropertiesElement ele) {
		System.out.println("StyleGraphicPropertiesElement " + ele);

	}

	@Override
	public void visit(StyleHandoutMasterElement ele) {
		System.out.println("StyleHandoutMasterElement " + ele);

	}

	@Override
	public void visit(StyleHeaderElement ele) {
		System.out.println("StyleHeaderElement " + ele);

	}

	@Override
	public void visit(StyleHeaderFooterPropertiesElement ele) {
		System.out.println("StyleHeaderFooterPropertiesElement " + ele);

	}

	@Override
	public void visit(StyleHeaderLeftElement ele) {
		System.out.println("StyleHeaderLeftElement " + ele);

	}

	@Override
	public void visit(StyleHeaderStyleElement ele) {
		System.out.println("StyleHeaderStyleElement " + ele);

	}

	@Override
	public void visit(StyleListLevelLabelAlignmentElement ele) {
		System.out.println("StyleListLevelLabelAlignmentElement " + ele);

	}

	@Override
	public void visit(StyleListLevelPropertiesElement ele) {
		System.out.println("StyleListLevelPropertiesElement " + ele);

	}

	@Override
	public void visit(StyleMapElement ele) {
		System.out.println("StyleMapElement " + ele);

	}

	@Override
	public void visit(StyleMasterPageElement ele) {
		System.out.println("StyleMasterPageElement " + ele);

	}

	@Override
	public void visit(StylePageLayoutElement ele) {
		System.out.println("StylePageLayoutElement " + ele);

	}

	@Override
	public void visit(StylePageLayoutPropertiesElement ele) {
		System.out.println("StylePageLayoutPropertiesElement " + ele);

	}

	@Override
	public void visit(StyleParagraphPropertiesElement ele) {
		System.out.println("StyleParagraphPropertiesElement " + ele);

	}

	@Override
	public void visit(StylePresentationPageLayoutElement ele) {
		System.out.println("StylePresentationPageLayoutElement " + ele);

	}

	@Override
	public void visit(StyleRegionCenterElement ele) {
		System.out.println("StyleRegionCenterElement " + ele);

	}

	@Override
	public void visit(StyleRegionLeftElement ele) {
		System.out.println("StyleRegionLeftElement " + ele);

	}

	@Override
	public void visit(StyleRegionRightElement ele) {
		System.out.println("StyleRegionRightElement " + ele);

	}

	@Override
	public void visit(StyleRubyPropertiesElement ele) {
		System.out.println("StyleRubyPropertiesElement " + ele);

	}

	@Override
	public void visit(StyleSectionPropertiesElement ele) {
		System.out.println("StyleSectionPropertiesElement " + ele);

	}

	@Override
	public void visit(StyleStyleElement ele) {
		System.out.println("StyleStyleElement " + ele);

	}

	@Override
	public void visit(StyleTabStopElement ele) {
		System.out.println("StyleTabStopElement " + ele);

	}

	@Override
	public void visit(StyleTabStopsElement ele) {
		System.out.println("StyleTabStopsElement " + ele);

	}

	@Override
	public void visit(StyleTableCellPropertiesElement ele) {
		System.out.println("StyleTableCellPropertiesElement " + ele);

	}

	@Override
	public void visit(StyleTableColumnPropertiesElement ele) {
		System.out.println("StyleTableColumnPropertiesElement " + ele);

	}

	@Override
	public void visit(StyleTablePropertiesElement ele) {
		System.out.println("StyleTablePropertiesElement " + ele);

	}

	@Override
	public void visit(StyleTableRowPropertiesElement ele) {
		System.out.println("StyleTableRowPropertiesElement " + ele);

	}

	@Override
	public void visit(StyleTextPropertiesElement ele) {
		System.out.println("StyleTextPropertiesElement " + ele);

	}

	@Override
	public void visit(SvgDefinitionSrcElement ele) {
		System.out.println("SvgDefinitionSrcElement " + ele);

	}

	@Override
	public void visit(SvgDescElement ele) {
		System.out.println("SvgDescElement " + ele);

	}

	@Override
	public void visit(SvgFontFaceFormatElement ele) {
		System.out.println("SvgFontFaceFormatElement " + ele);

	}

	@Override
	public void visit(SvgFontFaceNameElement ele) {
		System.out.println("SvgFontFaceNameElement " + ele);

	}

	@Override
	public void visit(SvgFontFaceSrcElement ele) {
		System.out.println("SvgFontFaceSrcElement " + ele);

	}

	@Override
	public void visit(SvgFontFaceUriElement ele) {
		System.out.println("SvgFontFaceUriElement " + ele);

	}

	@Override
	public void visit(SvgLinearGradientElement ele) {
		System.out.println("SvgLinearGradientElement " + ele);

	}

	@Override
	public void visit(SvgRadialGradientElement ele) {
		System.out.println("SvgRadialGradientElement " + ele);

	}

	@Override
	public void visit(SvgStopElement ele) {
		System.out.println("SvgStopElement " + ele);

	}

	@Override
	public void visit(SvgTitleElement ele) {
		System.out.println("SvgTitleElement " + ele);

	}

	@Override
	public void visit(TableBackgroundElement ele) {
		System.out.println("TableBackgroundElement " + ele);

	}

	@Override
	public void visit(TableBodyElement ele) {
		System.out.println("TableBodyElement " + ele);

	}

	@Override
	public void visit(TableCalculationSettingsElement ele) {
		System.out.println("TableCalculationSettingsElement " + ele);

	}

	@Override
	public void visit(TableCellAddressElement ele) {
		System.out.println("TableCellAddressElement " + ele);

	}

	@Override
	public void visit(TableCellContentChangeElement ele) {
		System.out.println("TableCellContentChangeElement " + ele);

	}

	@Override
	public void visit(TableCellContentDeletionElement ele) {
		System.out.println("TableCellContentDeletionElement " + ele);

	}

	@Override
	public void visit(TableCellRangeSourceElement ele) {
		System.out.println("TableCellRangeSourceElement " + ele);

	}

	@Override
	public void visit(TableChangeDeletionElement ele) {
		System.out.println("TableChangeDeletionElement " + ele);

	}

	@Override
	public void visit(TableChangeTrackTableCellElement ele) {
		System.out.println("TableChangeTrackTableCellElement " + ele);

	}

	@Override
	public void visit(TableConsolidationElement ele) {
		System.out.println("TableConsolidationElement " + ele);

	}

	@Override
	public void visit(TableContentValidationElement ele) {
		System.out.println("TableContentValidationElement " + ele);

	}

	@Override
	public void visit(TableContentValidationsElement ele) {
		System.out.println("TableContentValidationsElement " + ele);

	}

	@Override
	public void visit(TableCoveredTableCellElement ele) {
		System.out.println("TableCoveredTableCellElement " + ele);

	}

	@Override
	public void visit(TableCutOffsElement ele) {
		System.out.println("TableCutOffsElement " + ele);

	}

	@Override
	public void visit(TableDataPilotDisplayInfoElement ele) {
		System.out.println("TableDataPilotDisplayInfoElement " + ele);

	}

	@Override
	public void visit(TableDataPilotFieldElement ele) {
		System.out.println("TableDataPilotFieldElement " + ele);

	}

	@Override
	public void visit(TableDataPilotFieldReferenceElement ele) {
		System.out.println("TableDataPilotFieldReferenceElement " + ele);

	}

	@Override
	public void visit(TableDataPilotGroupElement ele) {
		System.out.println("TableDataPilotGroupElement " + ele);

	}

	@Override
	public void visit(TableDataPilotGroupMemberElement ele) {
		System.out.println("TableDataPilotGroupMemberElement " + ele);

	}

	@Override
	public void visit(TableDataPilotGroupsElement ele) {
		System.out.println("TableDataPilotGroupsElement " + ele);

	}

	@Override
	public void visit(TableDataPilotLayoutInfoElement ele) {
		System.out.println("TableDataPilotLayoutInfoElement " + ele);

	}

	@Override
	public void visit(TableDataPilotLevelElement ele) {
		System.out.println("TableDataPilotLevelElement " + ele);

	}

	@Override
	public void visit(TableDataPilotMemberElement ele) {
		System.out.println("TableDataPilotMemberElement " + ele);

	}

	@Override
	public void visit(TableDataPilotMembersElement ele) {
		System.out.println("TableDataPilotMembersElement " + ele);

	}

	@Override
	public void visit(TableDataPilotSortInfoElement ele) {
		System.out.println("TableDataPilotSortInfoElement " + ele);

	}

	@Override
	public void visit(TableDataPilotSubtotalElement ele) {
		System.out.println("TableDataPilotSubtotalElement " + ele);

	}

	@Override
	public void visit(TableDataPilotSubtotalsElement ele) {
		System.out.println("TableDataPilotSubtotalsElement " + ele);

	}

	@Override
	public void visit(TableDataPilotTableElement ele) {
		System.out.println("TableDataPilotTableElement " + ele);

	}

	@Override
	public void visit(TableDataPilotTablesElement ele) {
		System.out.println("TableDataPilotTablesElement " + ele);

	}

	@Override
	public void visit(TableDatabaseRangeElement ele) {
		System.out.println("TableDatabaseRangeElement " + ele);

	}

	@Override
	public void visit(TableDatabaseRangesElement ele) {
		System.out.println("TableDatabaseRangesElement " + ele);

	}

	@Override
	public void visit(TableDatabaseSourceQueryElement ele) {
		System.out.println("TableDatabaseSourceQueryElement " + ele);

	}

	@Override
	public void visit(TableDatabaseSourceSqlElement ele) {
		System.out.println("TableDatabaseSourceSqlElement " + ele);

	}

	@Override
	public void visit(TableDatabaseSourceTableElement ele) {
		System.out.println("TableDatabaseSourceTableElement " + ele);

	}

	@Override
	public void visit(TableDdeLinkElement ele) {
		System.out.println("TableDdeLinkElement " + ele);

	}

	@Override
	public void visit(TableDdeLinksElement ele) {
		System.out.println("TableDdeLinksElement " + ele);

	}

	@Override
	public void visit(TableDeletionElement ele) {
		System.out.println("TableDeletionElement " + ele);

	}

	@Override
	public void visit(TableDeletionsElement ele) {
		System.out.println("TableDeletionsElement " + ele);

	}

	@Override
	public void visit(TableDependenciesElement ele) {
		System.out.println("TableDependenciesElement " + ele);

	}

	@Override
	public void visit(TableDependencyElement ele) {
		System.out.println("TableDependencyElement " + ele);

	}

	@Override
	public void visit(TableDescElement ele) {
		System.out.println("TableDescElement " + ele);

	}

	@Override
	public void visit(TableDetectiveElement ele) {
		System.out.println("TableDetectiveElement " + ele);

	}

	@Override
	public void visit(TableErrorMacroElement ele) {
		System.out.println("TableErrorMacroElement " + ele);

	}

	@Override
	public void visit(TableErrorMessageElement ele) {
		System.out.println("TableErrorMessageElement " + ele);

	}

	@Override
	public void visit(TableEvenColumnsElement ele) {
		System.out.println("TableEvenColumnsElement " + ele);

	}

	@Override
	public void visit(TableEvenRowsElement ele) {
		System.out.println("TableEvenRowsElement " + ele);

	}

	@Override
	public void visit(TableFilterElement ele) {
		System.out.println("TableFilterElement " + ele);

	}

	@Override
	public void visit(TableFilterAndElement ele) {
		System.out.println("TableFilterAndElement " + ele);

	}

	@Override
	public void visit(TableFilterConditionElement ele) {
		System.out.println("TableFilterConditionElement " + ele);

	}

	@Override
	public void visit(TableFilterOrElement ele) {
		System.out.println("TableFilterOrElement " + ele);

	}

	@Override
	public void visit(TableFilterSetItemElement ele) {
		System.out.println("TableFilterSetItemElement " + ele);

	}

	@Override
	public void visit(TableFirstColumnElement ele) {
		System.out.println("TableFirstColumnElement " + ele);

	}

	@Override
	public void visit(TableFirstRowElement ele) {
		System.out.println("TableFirstRowElement " + ele);

	}

	@Override
	public void visit(TableHelpMessageElement ele) {
		System.out.println("TableHelpMessageElement " + ele);

	}

	@Override
	public void visit(TableHighlightedRangeElement ele) {
		System.out.println("TableHighlightedRangeElement " + ele);

	}

	@Override
	public void visit(TableInsertionElement ele) {
		System.out.println("TableInsertionElement " + ele);

	}

	@Override
	public void visit(TableInsertionCutOffElement ele) {
		System.out.println("TableInsertionCutOffElement " + ele);

	}

	@Override
	public void visit(TableIterationElement ele) {
		System.out.println("TableIterationElement " + ele);

	}

	@Override
	public void visit(TableLabelRangeElement ele) {
		System.out.println("TableLabelRangeElement " + ele);

	}

	@Override
	public void visit(TableLabelRangesElement ele) {
		System.out.println("TableLabelRangesElement " + ele);

	}

	@Override
	public void visit(TableLastColumnElement ele) {
		System.out.println("TableLastColumnElement " + ele);

	}

	@Override
	public void visit(TableLastRowElement ele) {
		System.out.println("TableLastRowElement " + ele);

	}

	@Override
	public void visit(TableMovementElement ele) {
		System.out.println("TableMovementElement " + ele);

	}

	@Override
	public void visit(TableMovementCutOffElement ele) {
		System.out.println("TableMovementCutOffElement " + ele);

	}

	@Override
	public void visit(TableNamedExpressionElement ele) {
		System.out.println("TableNamedExpressionElement " + ele);

	}

	@Override
	public void visit(TableNamedExpressionsElement ele) {
		System.out.println("TableNamedExpressionsElement " + ele);

	}

	@Override
	public void visit(TableNamedRangeElement ele) {
		System.out.println("TableNamedRangeElement " + ele);

	}

	@Override
	public void visit(TableNullDateElement ele) {
		System.out.println("TableNullDateElement " + ele);

	}

	@Override
	public void visit(TableOddColumnsElement ele) {
		System.out.println("TableOddColumnsElement " + ele);

	}

	@Override
	public void visit(TableOddRowsElement ele) {
		System.out.println("TableOddRowsElement " + ele);

	}

	@Override
	public void visit(TableOperationElement ele) {
		System.out.println("TableOperationElement " + ele);

	}

	@Override
	public void visit(TablePreviousElement ele) {
		System.out.println("TablePreviousElement " + ele);

	}

	@Override
	public void visit(TableScenarioElement ele) {
		System.out.println("TableScenarioElement " + ele);

	}

	@Override
	public void visit(TableShapesElement ele) {
		System.out.println("TableShapesElement " + ele);

	}

	@Override
	public void visit(TableSortElement ele) {
		System.out.println("TableSortElement " + ele);

	}

	@Override
	public void visit(TableSortByElement ele) {
		System.out.println("TableSortByElement " + ele);

	}

	@Override
	public void visit(TableSortGroupsElement ele) {
		System.out.println("TableSortGroupsElement " + ele);

	}

	@Override
	public void visit(TableSourceCellRangeElement ele) {
		System.out.println("TableSourceCellRangeElement " + ele);

	}

	@Override
	public void visit(TableSourceRangeAddressElement ele) {
		System.out.println("TableSourceRangeAddressElement " + ele);

	}

	@Override
	public void visit(TableSourceServiceElement ele) {
		System.out.println("TableSourceServiceElement " + ele);

	}

	@Override
	public void visit(TableSubtotalFieldElement ele) {
		System.out.println("TableSubtotalFieldElement " + ele);

	}

	@Override
	public void visit(TableSubtotalRuleElement ele) {
		System.out.println("TableSubtotalRuleElement " + ele);

	}

	@Override
	public void visit(TableSubtotalRulesElement ele) {
		System.out.println("TableSubtotalRulesElement " + ele);

	}

	@Override
	public void visit(TableTableElement ele) {
		System.out.println("TableTableElement " + ele);

	}

	@Override
	public void visit(TableTableCellElement ele) {
		System.out.println("TableTableCellElement " + ele);

	}

	@Override
	public void visit(TableTableColumnElement ele) {
		System.out.println("TableTableColumnElement " + ele);

	}

	@Override
	public void visit(TableTableColumnGroupElement ele) {
		System.out.println("TableTableColumnGroupElement " + ele);

	}

	@Override
	public void visit(TableTableColumnsElement ele) {
		System.out.println("TableTableColumnsElement " + ele);

	}

	@Override
	public void visit(TableTableHeaderColumnsElement ele) {
		System.out.println("TableTableHeaderColumnsElement " + ele);

	}

	@Override
	public void visit(TableTableHeaderRowsElement ele) {
		System.out.println("TableTableHeaderRowsElement " + ele);

	}

	@Override
	public void visit(TableTableRowElement ele) {
		System.out.println("TableTableRowElement " + ele);

	}

	@Override
	public void visit(TableTableRowGroupElement ele) {
		System.out.println("TableTableRowGroupElement " + ele);

	}

	@Override
	public void visit(TableTableRowsElement ele) {
		System.out.println("TableTableRowsElement " + ele);

	}

	@Override
	public void visit(TableTableSourceElement ele) {
		System.out.println("TableTableSourceElement " + ele);

	}

	@Override
	public void visit(TableTableTemplateElement ele) {
		System.out.println("TableTableTemplateElement " + ele);

	}

	@Override
	public void visit(TableTargetRangeAddressElement ele) {
		System.out.println("TableTargetRangeAddressElement " + ele);

	}

	@Override
	public void visit(TableTitleElement ele) {
		System.out.println("TableTitleElement " + ele);

	}

	@Override
	public void visit(TableTrackedChangesElement ele) {
		System.out.println("TableTrackedChangesElement " + ele);

	}

	@Override
	public void visit(TextAElement ele) {
		System.out.println("TextAElement " + ele);

	}

	@Override
	public void visit(TextAlphabeticalIndexElement ele) {
		System.out.println("TextAlphabeticalIndexElement " + ele);

	}

	@Override
	public void visit(TextAlphabeticalIndexAutoMarkFileElement ele) {
		System.out.println("TextAlphabeticalIndexAutoMarkFileElement " + ele);

	}

	@Override
	public void visit(TextAlphabeticalIndexEntryTemplateElement ele) {
		System.out.println("TextAlphabeticalIndexEntryTemplateElement " + ele);

	}

	@Override
	public void visit(TextAlphabeticalIndexMarkElement ele) {
		System.out.println("TextAlphabeticalIndexMarkElement " + ele);

	}

	@Override
	public void visit(TextAlphabeticalIndexMarkEndElement ele) {
		System.out.println("TextAlphabeticalIndexMarkEndElement " + ele);

	}

	@Override
	public void visit(TextAlphabeticalIndexMarkStartElement ele) {
		System.out.println("TextAlphabeticalIndexMarkStartElement " + ele);

	}

	@Override
	public void visit(TextAlphabeticalIndexSourceElement ele) {
		System.out.println("TextAlphabeticalIndexSourceElement " + ele);

	}

	@Override
	public void visit(TextAuthorInitialsElement ele) {
		System.out.println("TextAuthorInitialsElement " + ele);

	}

	@Override
	public void visit(TextAuthorNameElement ele) {
		System.out.println("TextAuthorNameElement " + ele);

	}

	@Override
	public void visit(TextBibliographyElement ele) {
		System.out.println("TextBibliographyElement " + ele);

	}

	@Override
	public void visit(TextBibliographyConfigurationElement ele) {
		System.out.println("TextBibliographyConfigurationElement " + ele);

	}

	@Override
	public void visit(TextBibliographyEntryTemplateElement ele) {
		System.out.println("TextBibliographyEntryTemplateElement " + ele);

	}

	@Override
	public void visit(TextBibliographyMarkElement ele) {
		System.out.println("TextBibliographyMarkElement " + ele);

	}

	@Override
	public void visit(TextBibliographySourceElement ele) {
		System.out.println("TextBibliographySourceElement " + ele);

	}

	@Override
	public void visit(TextBookmarkElement ele) {
		System.out.println("TextBookmarkElement " + ele);

	}

	@Override
	public void visit(TextBookmarkEndElement ele) {
		System.out.println("TextBookmarkEndElement " + ele);

	}

	@Override
	public void visit(TextBookmarkRefElement ele) {
		System.out.println("TextBookmarkRefElement " + ele);

	}

	@Override
	public void visit(TextBookmarkStartElement ele) {
		System.out.println("TextBookmarkStartElement " + ele);

	}

	@Override
	public void visit(TextChangeElement ele) {
		System.out.println("TextChangeElement " + ele);

	}

	@Override
	public void visit(TextChangeEndElement ele) {
		System.out.println("TextChangeEndElement " + ele);

	}

	@Override
	public void visit(TextChangeStartElement ele) {
		System.out.println("TextChangeStartElement " + ele);

	}

	@Override
	public void visit(TextChangedRegionElement ele) {
		System.out.println("TextChangedRegionElement " + ele);

	}

	@Override
	public void visit(TextChapterElement ele) {
		System.out.println("TextChapterElement " + ele);

	}

	@Override
	public void visit(TextCharacterCountElement ele) {
		System.out.println("TextCharacterCountElement " + ele);

	}

	@Override
	public void visit(TextConditionalTextElement ele) {
		System.out.println("TextConditionalTextElement " + ele);

	}

	@Override
	public void visit(TextCreationDateElement ele) {
		System.out.println("TextCreationDateElement " + ele);

	}

	@Override
	public void visit(TextCreationTimeElement ele) {
		System.out.println("TextCreationTimeElement " + ele);

	}

	@Override
	public void visit(TextCreatorElement ele) {
		System.out.println("TextCreatorElement " + ele);

	}

	@Override
	public void visit(TextDatabaseDisplayElement ele) {
		System.out.println("TextDatabaseDisplayElement " + ele);

	}

	@Override
	public void visit(TextDatabaseNameElement ele) {
		System.out.println("TextDatabaseNameElement " + ele);

	}

	@Override
	public void visit(TextDatabaseNextElement ele) {
		System.out.println("TextDatabaseNextElement " + ele);

	}

	@Override
	public void visit(TextDatabaseRowNumberElement ele) {
		System.out.println("TextDatabaseRowNumberElement " + ele);

	}

	@Override
	public void visit(TextDatabaseRowSelectElement ele) {
		System.out.println("TextDatabaseRowSelectElement " + ele);

	}

	@Override
	public void visit(TextDateElement ele) {
		System.out.println("TextDateElement " + ele);

	}

	@Override
	public void visit(TextDdeConnectionElement ele) {
		System.out.println("TextDdeConnectionElement " + ele);

	}

	@Override
	public void visit(TextDdeConnectionDeclElement ele) {
		System.out.println("TextDdeConnectionDeclElement " + ele);

	}

	@Override
	public void visit(TextDdeConnectionDeclsElement ele) {
		System.out.println("TextDdeConnectionDeclsElement " + ele);

	}

	@Override
	public void visit(TextDeletionElement ele) {
		System.out.println("TextDeletionElement " + ele);

	}

	@Override
	public void visit(TextDescriptionElement ele) {
		System.out.println("TextDescriptionElement " + ele);

	}

	@Override
	public void visit(TextEditingCyclesElement ele) {
		System.out.println("TextEditingCyclesElement " + ele);

	}

	@Override
	public void visit(TextEditingDurationElement ele) {
		System.out.println("TextEditingDurationElement " + ele);

	}

	@Override
	public void visit(TextExecuteMacroElement ele) {
		System.out.println("TextExecuteMacroElement " + ele);

	}

	@Override
	public void visit(TextExpressionElement ele) {
		System.out.println("TextExpressionElement " + ele);

	}

	@Override
	public void visit(TextFileNameElement ele) {
		System.out.println("TextFileNameElement " + ele);

	}

	@Override
	public void visit(TextFormatChangeElement ele) {
		System.out.println("TextFormatChangeElement " + ele);

	}

	@Override
	public void visit(TextHElement ele) {
		System.out.println("TextHElement " + ele);

	}

	@Override
	public void visit(TextHiddenParagraphElement ele) {
		System.out.println("TextHiddenParagraphElement " + ele);

	}

	@Override
	public void visit(TextHiddenTextElement ele) {
		System.out.println("TextHiddenTextElement " + ele);

	}

	@Override
	public void visit(TextIllustrationIndexElement ele) {
		System.out.println("TextIllustrationIndexElement " + ele);

	}

	@Override
	public void visit(TextIllustrationIndexEntryTemplateElement ele) {
		System.out.println("TextIllustrationIndexEntryTemplateElement " + ele);

	}

	@Override
	public void visit(TextIllustrationIndexSourceElement ele) {
		System.out.println("TextIllustrationIndexSourceElement " + ele);

	}

	@Override
	public void visit(TextImageCountElement ele) {
		System.out.println("TextImageCountElement " + ele);

	}

	@Override
	public void visit(TextIndexBodyElement ele) {
		System.out.println("TextIndexBodyElement " + ele);

	}

	@Override
	public void visit(TextIndexEntryBibliographyElement ele) {
		System.out.println("TextIndexEntryBibliographyElement " + ele);

	}

	@Override
	public void visit(TextIndexEntryChapterElement ele) {
		System.out.println("TextIndexEntryChapterElement " + ele);

	}

	@Override
	public void visit(TextIndexEntryLinkEndElement ele) {
		System.out.println("TextIndexEntryLinkEndElement " + ele);

	}

	@Override
	public void visit(TextIndexEntryLinkStartElement ele) {
		System.out.println("TextIndexEntryLinkStartElement " + ele);

	}

	@Override
	public void visit(TextIndexEntryPageNumberElement ele) {
		System.out.println("TextIndexEntryPageNumberElement " + ele);

	}

	@Override
	public void visit(TextIndexEntrySpanElement ele) {
		System.out.println("TextIndexEntrySpanElement " + ele);

	}

	@Override
	public void visit(TextIndexEntryTabStopElement ele) {
		System.out.println("TextIndexEntryTabStopElement " + ele);

	}

	@Override
	public void visit(TextIndexEntryTextElement ele) {
		System.out.println("TextIndexEntryTextElement " + ele);

	}

	@Override
	public void visit(TextIndexSourceStyleElement ele) {
		System.out.println("TextIndexSourceStyleElement " + ele);

	}

	@Override
	public void visit(TextIndexSourceStylesElement ele) {
		System.out.println("TextIndexSourceStylesElement " + ele);

	}

	@Override
	public void visit(TextIndexTitleElement ele) {
		System.out.println("TextIndexTitleElement " + ele);

	}

	@Override
	public void visit(TextIndexTitleTemplateElement ele) {
		System.out.println("TextIndexTitleTemplateElement " + ele);

	}

	@Override
	public void visit(TextInitialCreatorElement ele) {
		System.out.println("TextInitialCreatorElement " + ele);

	}

	@Override
	public void visit(TextInsertionElement ele) {
		System.out.println("TextInsertionElement " + ele);

	}

	@Override
	public void visit(TextKeywordsElement ele) {
		System.out.println("TextKeywordsElement " + ele);

	}

	@Override
	public void visit(TextLineBreakElement ele) {
		System.out.println("TextLineBreakElement " + ele);

	}

	@Override
	public void visit(TextLinenumberingConfigurationElement ele) {
		System.out.println("TextLinenumberingConfigurationElement " + ele);

	}

	@Override
	public void visit(TextLinenumberingSeparatorElement ele) {
		System.out.println("TextLinenumberingSeparatorElement " + ele);

	}

	@Override
	public void visit(TextListElement ele) {
		System.out.println("TextListElement " + ele);

	}

	@Override
	public void visit(TextListHeaderElement ele) {
		System.out.println("TextListHeaderElement " + ele);

	}

	@Override
	public void visit(TextListItemElement ele) {
		System.out.println("TextListItemElement " + ele);

	}

	@Override
	public void visit(TextListLevelStyleBulletElement ele) {
		System.out.println("TextListLevelStyleBulletElement " + ele);

	}

	@Override
	public void visit(TextListLevelStyleImageElement ele) {
		System.out.println("TextListLevelStyleImageElement " + ele);

	}

	@Override
	public void visit(TextListLevelStyleNumberElement ele) {
		System.out.println("TextListLevelStyleNumberElement " + ele);

	}

	@Override
	public void visit(TextListStyleElement ele) {
		System.out.println("TextListStyleElement " + ele);

	}

	@Override
	public void visit(TextMeasureElement ele) {
		System.out.println("TextMeasureElement " + ele);

	}

	@Override
	public void visit(TextMetaElement ele) {
		System.out.println("TextMetaElement " + ele);

	}

	@Override
	public void visit(TextMetaFieldElement ele) {
		System.out.println("TextMetaFieldElement " + ele);

	}

	@Override
	public void visit(TextModificationDateElement ele) {
		System.out.println("TextModificationDateElement " + ele);

	}

	@Override
	public void visit(TextModificationTimeElement ele) {
		System.out.println("TextModificationTimeElement " + ele);

	}

	@Override
	public void visit(TextNoteElement ele) {
		System.out.println("TextNoteElement " + ele);

	}

	@Override
	public void visit(TextNoteBodyElement ele) {
		System.out.println("TextNoteBodyElement " + ele);

	}

	@Override
	public void visit(TextNoteCitationElement ele) {
		System.out.println("TextNoteCitationElement " + ele);

	}

	@Override
	public void visit(TextNoteContinuationNoticeBackwardElement ele) {
		System.out.println("TextNoteContinuationNoticeBackwardElement " + ele);

	}

	@Override
	public void visit(TextNoteContinuationNoticeForwardElement ele) {
		System.out.println("TextNoteContinuationNoticeForwardElement " + ele);

	}

	@Override
	public void visit(TextNoteRefElement ele) {
		System.out.println("TextNoteRefElement " + ele);

	}

	@Override
	public void visit(TextNotesConfigurationElement ele) {
		System.out.println("TextNotesConfigurationElement " + ele);

	}

	@Override
	public void visit(TextNumberElement ele) {
		System.out.println("TextNumberElement " + ele);

	}

	@Override
	public void visit(TextNumberedParagraphElement ele) {
		System.out.println("TextNumberedParagraphElement " + ele);

	}

	@Override
	public void visit(TextObjectCountElement ele) {
		System.out.println("TextObjectCountElement " + ele);

	}

	@Override
	public void visit(TextObjectIndexElement ele) {
		System.out.println("TextObjectIndexElement " + ele);

	}

	@Override
	public void visit(TextObjectIndexEntryTemplateElement ele) {
		System.out.println("TextObjectIndexEntryTemplateElement " + ele);

	}

	@Override
	public void visit(TextObjectIndexSourceElement ele) {
		System.out.println("TextObjectIndexSourceElement " + ele);

	}

	@Override
	public void visit(TextOutlineLevelStyleElement ele) {
		System.out.println("TextOutlineLevelStyleElement " + ele);

	}

	@Override
	public void visit(TextOutlineStyleElement ele) {
		System.out.println("TextOutlineStyleElement " + ele);

	}

	@Override
	public void visit(TextPElement ele) {
		System.out.println("TextPElement " + ele);

	}

	@Override
	public void visit(TextPageElement ele) {
		System.out.println("TextPageElement " + ele);

	}

	@Override
	public void visit(TextPageContinuationElement ele) {
		System.out.println("TextPageContinuationElement " + ele);

	}

	@Override
	public void visit(TextPageCountElement ele) {
		System.out.println("TextPageCountElement " + ele);

	}

	@Override
	public void visit(TextPageNumberElement ele) {
		System.out.println("TextPageNumberElement " + ele);

	}

	@Override
	public void visit(TextPageSequenceElement ele) {
		System.out.println("TextPageSequenceElement " + ele);

	}

	@Override
	public void visit(TextPageVariableGetElement ele) {
		System.out.println("TextPageVariableGetElement " + ele);

	}

	@Override
	public void visit(TextPageVariableSetElement ele) {
		System.out.println("TextPageVariableSetElement " + ele);

	}

	@Override
	public void visit(TextParagraphCountElement ele) {
		System.out.println("TextParagraphCountElement " + ele);

	}

	@Override
	public void visit(TextPlaceholderElement ele) {
		System.out.println("TextPlaceholderElement " + ele);

	}

	@Override
	public void visit(TextPrintDateElement ele) {
		System.out.println("TextPrintDateElement " + ele);

	}

	@Override
	public void visit(TextPrintTimeElement ele) {
		System.out.println("TextPrintTimeElement " + ele);

	}

	@Override
	public void visit(TextPrintedByElement ele) {
		System.out.println("TextPrintedByElement " + ele);

	}

	@Override
	public void visit(TextReferenceMarkElement ele) {
		System.out.println("TextReferenceMarkElement " + ele);

	}

	@Override
	public void visit(TextReferenceMarkEndElement ele) {
		System.out.println("TextReferenceMarkEndElement " + ele);

	}

	@Override
	public void visit(TextReferenceMarkStartElement ele) {
		System.out.println("TextReferenceMarkStartElement " + ele);

	}

	@Override
	public void visit(TextReferenceRefElement ele) {
		System.out.println("TextReferenceRefElement " + ele);

	}

	@Override
	public void visit(TextRubyElement ele) {
		System.out.println("TextRubyElement " + ele);

	}

	@Override
	public void visit(TextRubyBaseElement ele) {
		System.out.println("TextRubyBaseElement " + ele);

	}

	@Override
	public void visit(TextRubyTextElement ele) {
		System.out.println("TextRubyTextElement " + ele);

	}

	@Override
	public void visit(TextSElement ele) {
		System.out.println("TextSElement " + ele);

	}

	@Override
	public void visit(TextScriptElement ele) {
		System.out.println("TextScriptElement " + ele);

	}

	@Override
	public void visit(TextSectionElement ele) {
		System.out.println("TextSectionElement " + ele);

	}

	@Override
	public void visit(TextSectionSourceElement ele) {
		System.out.println("TextSectionSourceElement " + ele);

	}

	@Override
	public void visit(TextSenderCityElement ele) {
		System.out.println("TextSenderCityElement " + ele);

	}

	@Override
	public void visit(TextSenderCompanyElement ele) {
		System.out.println("TextSenderCompanyElement " + ele);

	}

	@Override
	public void visit(TextSenderCountryElement ele) {
		System.out.println("TextSenderCountryElement " + ele);

	}

	@Override
	public void visit(TextSenderEmailElement ele) {
		System.out.println("TextSenderEmailElement " + ele);

	}

	@Override
	public void visit(TextSenderFaxElement ele) {
		System.out.println("TextSenderFaxElement " + ele);

	}

	@Override
	public void visit(TextSenderFirstnameElement ele) {
		System.out.println("TextSenderFirstnameElement " + ele);

	}

	@Override
	public void visit(TextSenderInitialsElement ele) {
		System.out.println("TextSenderInitialsElement " + ele);

	}

	@Override
	public void visit(TextSenderLastnameElement ele) {
		System.out.println("TextSenderLastnameElement " + ele);

	}

	@Override
	public void visit(TextSenderPhonePrivateElement ele) {
		System.out.println("TextSenderPhonePrivateElement " + ele);

	}

	@Override
	public void visit(TextSenderPhoneWorkElement ele) {
		System.out.println("TextSenderPhoneWorkElement " + ele);

	}

	@Override
	public void visit(TextSenderPositionElement ele) {
		System.out.println("TextSenderPositionElement " + ele);

	}

	@Override
	public void visit(TextSenderPostalCodeElement ele) {
		System.out.println("TextSenderPostalCodeElement " + ele);

	}

	@Override
	public void visit(TextSenderStateOrProvinceElement ele) {
		System.out.println("TextSenderStateOrProvinceElement " + ele);

	}

	@Override
	public void visit(TextSenderStreetElement ele) {
		System.out.println("TextSenderStreetElement " + ele);

	}

	@Override
	public void visit(TextSenderTitleElement ele) {
		System.out.println("TextSenderTitleElement " + ele);

	}

	@Override
	public void visit(TextSequenceElement ele) {
		System.out.println("TextSequenceElement " + ele);

	}

	@Override
	public void visit(TextSequenceDeclElement ele) {
		System.out.println("TextSequenceDeclElement " + ele);

	}

	@Override
	public void visit(TextSequenceDeclsElement ele) {
		System.out.println("TextSequenceDeclsElement " + ele);

	}

	@Override
	public void visit(TextSequenceRefElement ele) {
		System.out.println("TextSequenceRefElement " + ele);

	}

	@Override
	public void visit(TextSheetNameElement ele) {
		System.out.println("TextSheetNameElement " + ele);

	}

	@Override
	public void visit(TextSoftPageBreakElement ele) {
		System.out.println("TextSoftPageBreakElement " + ele);

	}

	@Override
	public void visit(TextSortKeyElement ele) {
		System.out.println("TextSortKeyElement " + ele);

	}

	@Override
	public void visit(TextSpanElement ele) {
		System.out.println("TextSpanElement " + ele);

	}

	@Override
	public void visit(TextSubjectElement ele) {
		System.out.println("TextSubjectElement " + ele);

	}

	@Override
	public void visit(TextTabElement ele) {
		System.out.println("TextTabElement " + ele);

	}

	@Override
	public void visit(TextTableCountElement ele) {
		System.out.println("TextTableCountElement " + ele);

	}

	@Override
	public void visit(TextTableFormulaElement ele) {
		System.out.println("TextTableFormulaElement " + ele);

	}

	@Override
	public void visit(TextTableIndexElement ele) {
		System.out.println("TextTableIndexElement " + ele);

	}

	@Override
	public void visit(TextTableIndexEntryTemplateElement ele) {
		System.out.println("TextTableIndexEntryTemplateElement " + ele);

	}

	@Override
	public void visit(TextTableIndexSourceElement ele) {
		System.out.println("TextTableIndexSourceElement " + ele);

	}

	@Override
	public void visit(TextTableOfContentElement ele) {
		System.out.println("TextTableOfContentElement " + ele);

	}

	@Override
	public void visit(TextTableOfContentEntryTemplateElement ele) {
		System.out.println("TextTableOfContentEntryTemplateElement " + ele);

	}

	@Override
	public void visit(TextTableOfContentSourceElement ele) {
		System.out.println("TextTableOfContentSourceElement " + ele);

	}

	@Override
	public void visit(TextTemplateNameElement ele) {
		System.out.println("TextTemplateNameElement " + ele);

	}

	@Override
	public void visit(TextTextInputElement ele) {
		System.out.println("TextTextInputElement " + ele);

	}

	@Override
	public void visit(TextTimeElement ele) {
		System.out.println("TextTimeElement " + ele);

	}

	@Override
	public void visit(TextTitleElement ele) {
		System.out.println("TextTitleElement " + ele);

	}

	@Override
	public void visit(TextTocMarkElement ele) {
		System.out.println("TextTocMarkElement " + ele);

	}

	@Override
	public void visit(TextTocMarkEndElement ele) {
		System.out.println("TextTocMarkEndElement " + ele);

	}

	@Override
	public void visit(TextTocMarkStartElement ele) {
		System.out.println("TextTocMarkStartElement " + ele);

	}

	@Override
	public void visit(TextTrackedChangesElement ele) {
		System.out.println("TextTrackedChangesElement " + ele);

	}

	@Override
	public void visit(TextUserDefinedElement ele) {
		System.out.println("TextUserDefinedElement " + ele);

	}

	@Override
	public void visit(TextUserFieldDeclElement ele) {
		System.out.println("TextUserFieldDeclElement " + ele);

	}

	@Override
	public void visit(TextUserFieldDeclsElement ele) {
		System.out.println("TextUserFieldDeclsElement " + ele);

	}

	@Override
	public void visit(TextUserFieldGetElement ele) {
		System.out.println("TextUserFieldGetElement " + ele);

	}

	@Override
	public void visit(TextUserFieldInputElement ele) {
		System.out.println("TextUserFieldInputElement " + ele);

	}

	@Override
	public void visit(TextUserIndexElement ele) {
		System.out.println("TextUserIndexElement " + ele);

	}

	@Override
	public void visit(TextUserIndexEntryTemplateElement ele) {
		System.out.println("TextUserIndexEntryTemplateElement " + ele);

	}

	@Override
	public void visit(TextUserIndexMarkElement ele) {
		System.out.println("TextUserIndexMarkElement " + ele);

	}

	@Override
	public void visit(TextUserIndexMarkEndElement ele) {
		System.out.println("TextUserIndexMarkEndElement " + ele);

	}

	@Override
	public void visit(TextUserIndexMarkStartElement ele) {
		System.out.println("TextUserIndexMarkStartElement " + ele);

	}

	@Override
	public void visit(TextUserIndexSourceElement ele) {
		System.out.println("TextUserIndexSourceElement " + ele);

	}

	@Override
	public void visit(TextVariableDeclElement ele) {
		System.out.println("TextVariableDeclElement " + ele);

	}

	@Override
	public void visit(TextVariableDeclsElement ele) {
		System.out.println("TextVariableDeclsElement " + ele);

	}

	@Override
	public void visit(TextVariableGetElement ele) {
		System.out.println("TextVariableGetElement " + ele);

	}

	@Override
	public void visit(TextVariableInputElement ele) {
		System.out.println("TextVariableInputElement " + ele);

	}

	@Override
	public void visit(TextVariableSetElement ele) {
		System.out.println("TextVariableSetElement " + ele);

	}

	@Override
	public void visit(TextWordCountElement ele) {
		System.out.println("TextWordCountElement " + ele);
	}

	@Override
	public void visit(XformsModelElement ele) {
		System.out.println("XformsModelElement " + ele);
	}

}