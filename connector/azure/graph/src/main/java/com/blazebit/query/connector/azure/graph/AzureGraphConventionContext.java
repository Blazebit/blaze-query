/*
 * SPDX-License-Identifier: Apache-2.0
 * Copyright Blazebit
 */
package com.blazebit.query.connector.azure.graph;

import java.lang.reflect.Member;

import com.blazebit.query.connector.base.ConventionContext;
import com.microsoft.graph.beta.models.AccessReviewInstanceDecisionItem;
import com.microsoft.graph.beta.models.ActivityHistoryItem;
import com.microsoft.graph.beta.models.BaseItem;
import com.microsoft.graph.beta.models.ChatMessage;
import com.microsoft.graph.beta.models.ColumnDefinition;
import com.microsoft.graph.beta.models.Connector;
import com.microsoft.graph.beta.models.ConnectorGroup;
import com.microsoft.graph.beta.models.ContactFolder;
import com.microsoft.graph.beta.models.ContentType;
import com.microsoft.graph.beta.models.DetectedApp;
import com.microsoft.graph.beta.models.DriveItem;
import com.microsoft.graph.beta.models.Event;
import com.microsoft.graph.beta.models.ItemActivity;
import com.microsoft.graph.beta.models.ItemActivityOLD;
import com.microsoft.graph.beta.models.List;
import com.microsoft.graph.beta.models.ListItem;
import com.microsoft.graph.beta.models.MailFolder;
import com.microsoft.graph.beta.models.OnenotePage;
import com.microsoft.graph.beta.models.ParentLabelDetails;
import com.microsoft.graph.beta.models.PlannerTeamsPublicationInfo;
import com.microsoft.graph.beta.models.Post;
import com.microsoft.graph.beta.models.PrintTaskDefinition;
import com.microsoft.graph.beta.models.PrinterShare;
import com.microsoft.graph.beta.models.SectionGroup;
import com.microsoft.graph.beta.models.SensitivityLabel;
import com.microsoft.graph.beta.models.Site;
import com.microsoft.graph.beta.models.StringKeyAttributeMappingSourceValuePair;
import com.microsoft.graph.beta.models.Team;
import com.microsoft.graph.beta.models.TeamInfo;
import com.microsoft.graph.beta.models.VirtualEventPresenter;
import com.microsoft.graph.beta.models.WorkbookChart;
import com.microsoft.graph.beta.models.WorkbookNamedItem;
import com.microsoft.graph.beta.models.WorkbookOperationError;
import com.microsoft.graph.beta.models.WorkbookPivotTable;
import com.microsoft.graph.beta.models.WorkbookTable;
import com.microsoft.graph.beta.models.termstore.Group;
import com.microsoft.graph.beta.models.termstore.Relation;
import com.microsoft.graph.beta.models.termstore.Set;
import com.microsoft.graph.beta.models.termstore.Term;

/**
 * A method filter to exclude internal and cyclic methods from the Azure Graph models.
 *
 * @author Christian Beikov
 * @since 1.0.0
 */
public class AzureGraphConventionContext implements ConventionContext {

	public static final ConventionContext INSTANCE = new AzureGraphConventionContext();

	private AzureGraphConventionContext() {
	}

	@Override
	public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
		switch ( member.getName() ) {
			case "getFieldDeserializers":
			case "getBackingStore":
				return null;
			// There are cycles in the model
			case "getActivity":
				return member.getDeclaringClass() != ActivityHistoryItem.class ? this
						: NestedUserActivityConventionContext.INSTANCE;
			case "getApplications":
				return member.getDeclaringClass() != ConnectorGroup.class ? this
						: NestedApplicationConventionContext.INSTANCE;
			case "getMemberOf":
				return member.getDeclaringClass() != Connector.class ? this : NestedConnectorConventionContext.INSTANCE;
			case "getCalendar":
				return member.getDeclaringClass() != Event.class ? this : NestedCalendarConventionContext.INSTANCE;
			case "getExceptionOccurrences":
			case "getInstances":
				return member.getDeclaringClass() != Event.class ? this : NestedEventConventionContext.INSTANCE;
			case "getInstance":
				return member.getDeclaringClass() != AccessReviewInstanceDecisionItem.class ? this
						: NestedAccessReviewInstanceConventionContext.INSTANCE;
			case "getReplies":
				return member.getDeclaringClass() != ChatMessage.class ? this
						: NestedChatMessageConventionContext.INSTANCE;
			case "getValue":
				return member.getDeclaringClass() != StringKeyAttributeMappingSourceValuePair.class ? this : null;
			case "getChildFolders":
				if ( member.getDeclaringClass() == ContactFolder.class ) {
					return NestedContactFolderConventionContext.INSTANCE;
				}
				if ( member.getDeclaringClass() == MailFolder.class ) {
					return NestedMailFolderConventionContext.INSTANCE;
				}
				return this;
			case "getSessions":
				return member.getDeclaringClass() != VirtualEventPresenter.class ? this
						: NestedVirtualEventSessionConventionContext.INSTANCE;
			case "getPrinter":
				return member.getDeclaringClass() != PrinterShare.class ? this
						: NestedPrinterConventionContext.INSTANCE;
			case "getTasks":
				return member.getDeclaringClass() != PrintTaskDefinition.class ? this
						: NestedPrintTaskConventionContext.INSTANCE;
			case "getParent":
				if ( member.getDeclaringClass() == ParentLabelDetails.class ) {
					return NestedParentLabelDetailsConventionContext.INSTANCE;
				}
				if ( member.getDeclaringClass() == com.microsoft.graph.beta.models.security.SensitivityLabel.class ) {
					return NestedSecuritySensitivityLabelConventionContext.INSTANCE;
				}
				return this;
			case "getParentNotebook":
				return member.getDeclaringClass() != SectionGroup.class ? this
						: NestedNotebookConventionContext.INSTANCE;
			case "getParentSectionGroup":
				return member.getDeclaringClass() != SectionGroup.class ? this
						: NestedSectionGroupConventionContext.INSTANCE;
			case "getParentSection":
				return member.getDeclaringClass() != OnenotePage.class ? this
						: NestedOnenoteSectionConventionContext.INSTANCE;
			case "getChildren":
				if ( member.getDeclaringClass() == DriveItem.class ) {
					return NestedDriveItemConventionContext.INSTANCE;
				}
				if ( member.getDeclaringClass() == Term.class || member.getDeclaringClass() == Set.class ) {
					return NestedTermConventionContext.INSTANCE;
				}
				return this;
			case "getSublabels":
				return member.getDeclaringClass() != SensitivityLabel.class ? this
						: NestedSensitivityLabelConventionContext.INSTANCE;
			case "getTeamsPublicationInfo":
				return concreteClass != PlannerTeamsPublicationInfo.class ? this
						: NestedPlannerTeamsPublicationInfoConventionContext.INSTANCE;
			case "getManagedDevices":
				return concreteClass != DetectedApp.class ? this : NestedManagedDeviceConventionContext.INSTANCE;
			case "getGroup":
				return member.getDeclaringClass() != Team.class ? this : NestedGroupConventionContext.INSTANCE;
			case "getTeam":
				return member.getDeclaringClass() != TeamInfo.class ? this : NestedTeamConventionContext.INSTANCE;
			case "getInReplyTo":
				return member.getDeclaringClass() != Post.class ? this : NestedPostConventionContext.INSTANCE;
			case "getSites":
				return member.getDeclaringClass() != Site.class ? this : NestedSiteConventionContext.INSTANCE;
			case "getSet":
				return member.getDeclaringClass() != Relation.class ? this : NestedSetConventionContext.INSTANCE;
			case "getSets":
				return member.getDeclaringClass() != Group.class ? this : NestedSetConventionContext.INSTANCE;
			case "getParentGroup":
				return member.getDeclaringClass() != Group.class ? this : NestedGroupConventionContext.INSTANCE;
			case "getDrive":
				return member.getDeclaringClass() != List.class ? this : NestedDriveConventionContext.INSTANCE;
			case "getDriveItem":
				return member.getDeclaringClass() != ItemActivity.class
						&& member.getDeclaringClass() != ItemActivityOLD.class
						&& member.getDeclaringClass() != ListItem.class ? this
						: NestedDriveItemConventionContext.INSTANCE;
			case "getActivities":
				return member.getDeclaringClass() != ListItem.class ? this
						: NestedItemActivityConventionContext.INSTANCE;
			case "getWorksheet":
				return member.getDeclaringClass() != WorkbookChart.class
						&& member.getDeclaringClass() != WorkbookNamedItem.class
						&& member.getDeclaringClass() != WorkbookTable.class
						&& member.getDeclaringClass() != WorkbookPivotTable.class ? this
						: NestedWorksheetConventionContext.INSTANCE;
			case "getSourceColumn":
				return member.getDeclaringClass() != ColumnDefinition.class ? this : null;
			case "getInnerError":
				return member.getDeclaringClass() != WorkbookOperationError.class ? this : null;
			case "getBase":
			case "getBaseTypes":
				return member.getDeclaringClass() != ContentType.class ? this
						: NestedContentTypeConventionContext.INSTANCE;
			case "getTerms":
				return member.getDeclaringClass() != Set.class ? this : NestedTermConventionContext.INSTANCE;
			case "getFromTerm":
			case "getToTerm":
				return member.getDeclaringClass() != Relation.class ? this : NestedTermConventionContext.INSTANCE;
			case "getCreatedByUser":
			case "getLastModifiedByUser":
				return member.getDeclaringClass() != BaseItem.class ? this : NestedUserConventionContext.INSTANCE;
			default:
				return this;
		}
	}

	private static final class NestedUserConventionContext implements ConventionContext {

		private static final NestedUserConventionContext INSTANCE = new NestedUserConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedTermConventionContext implements ConventionContext {

		private static final NestedTermConventionContext INSTANCE = new NestedTermConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedDriveItemConventionContext implements ConventionContext {

		private static final NestedDriveItemConventionContext INSTANCE = new NestedDriveItemConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedContactFolderConventionContext implements ConventionContext {

		private static final NestedContactFolderConventionContext INSTANCE = new NestedContactFolderConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedUserActivityConventionContext implements ConventionContext {

		private static final NestedUserActivityConventionContext INSTANCE = new NestedUserActivityConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedCalendarConventionContext implements ConventionContext {

		private static final NestedCalendarConventionContext INSTANCE = new NestedCalendarConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedEventConventionContext implements ConventionContext {

		private static final NestedEventConventionContext INSTANCE = new NestedEventConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedChatMessageConventionContext implements ConventionContext {

		private static final NestedChatMessageConventionContext INSTANCE = new NestedChatMessageConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedWorksheetConventionContext implements ConventionContext {

		private static final NestedWorksheetConventionContext INSTANCE = new NestedWorksheetConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedSetConventionContext implements ConventionContext {

		private static final NestedSetConventionContext INSTANCE = new NestedSetConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedGroupConventionContext implements ConventionContext {

		private static final NestedGroupConventionContext INSTANCE = new NestedGroupConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedContentTypeConventionContext implements ConventionContext {

		private static final NestedContentTypeConventionContext INSTANCE = new NestedContentTypeConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedDriveConventionContext implements ConventionContext {

		private static final NestedDriveConventionContext INSTANCE = new NestedDriveConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedNotebookConventionContext implements ConventionContext {

		private static final NestedNotebookConventionContext INSTANCE = new NestedNotebookConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedSectionGroupConventionContext implements ConventionContext {

		private static final NestedSectionGroupConventionContext INSTANCE = new NestedSectionGroupConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedOnenoteSectionConventionContext implements ConventionContext {

		private static final NestedOnenoteSectionConventionContext INSTANCE = new NestedOnenoteSectionConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedSiteConventionContext implements ConventionContext {

		private static final NestedSiteConventionContext INSTANCE = new NestedSiteConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedTeamConventionContext implements ConventionContext {

		private static final NestedTeamConventionContext INSTANCE = new NestedTeamConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedPostConventionContext implements ConventionContext {

		private static final NestedPostConventionContext INSTANCE = new NestedPostConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedMailFolderConventionContext implements ConventionContext {

		private static final NestedMailFolderConventionContext INSTANCE = new NestedMailFolderConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedPrinterConventionContext implements ConventionContext {

		private static final NestedPrinterConventionContext INSTANCE = new NestedPrinterConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedPrintTaskConventionContext implements ConventionContext {

		private static final NestedPrintTaskConventionContext INSTANCE = new NestedPrintTaskConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedApplicationConventionContext implements ConventionContext {

		private static final NestedApplicationConventionContext INSTANCE = new NestedApplicationConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedConnectorConventionContext implements ConventionContext {

		private static final NestedConnectorConventionContext INSTANCE = new NestedConnectorConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedItemActivityConventionContext implements ConventionContext {

		private static final NestedItemActivityConventionContext INSTANCE = new NestedItemActivityConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedParentLabelDetailsConventionContext implements ConventionContext {

		private static final NestedParentLabelDetailsConventionContext INSTANCE = new NestedParentLabelDetailsConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedSensitivityLabelConventionContext implements ConventionContext {

		private static final NestedSensitivityLabelConventionContext INSTANCE = new NestedSensitivityLabelConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedPlannerTeamsPublicationInfoConventionContext implements ConventionContext {

		private static final NestedPlannerTeamsPublicationInfoConventionContext INSTANCE = new NestedPlannerTeamsPublicationInfoConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedManagedDeviceConventionContext implements ConventionContext {

		private static final NestedManagedDeviceConventionContext INSTANCE = new NestedManagedDeviceConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedAccessReviewInstanceConventionContext implements ConventionContext {

		private static final NestedAccessReviewInstanceConventionContext INSTANCE = new NestedAccessReviewInstanceConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedSecuritySensitivityLabelConventionContext implements ConventionContext {

		private static final NestedSecuritySensitivityLabelConventionContext INSTANCE = new NestedSecuritySensitivityLabelConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

	private static final class NestedVirtualEventSessionConventionContext implements ConventionContext {

		private static final NestedVirtualEventSessionConventionContext INSTANCE = new NestedVirtualEventSessionConventionContext();

		@Override
		public ConventionContext getSubFilter(Class<?> concreteClass, Member member) {
			switch ( member.getName() ) {
				case "getFieldDeserializers":
				case "getBackingStore":
					return null;
				// Filter out cycles in the model
				case "getId":
					return this;
				default:
					return null;
			}
		}
	}

}
