/*
 * Copyright 2024 - 2024 Blazebit.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.blazebit.query.connector.azure.graph;

import java.lang.reflect.Method;

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
    public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
        switch (method.getName()) {
            case "getFieldDeserializers":
            case "getBackingStore":
                return null;
            // There are cycles in the model
            case "getActivity":
                return method.getDeclaringClass() != ActivityHistoryItem.class ? this : NestedUserActivityConventionContext.INSTANCE;
            case "getApplications":
                return method.getDeclaringClass() != ConnectorGroup.class ? this : NestedApplicationConventionContext.INSTANCE;
            case "getMemberOf":
                return method.getDeclaringClass() != Connector.class ? this : NestedConnectorConventionContext.INSTANCE;
            case "getCalendar":
                return method.getDeclaringClass() != Event.class ? this : NestedCalendarConventionContext.INSTANCE;
            case "getExceptionOccurrences":
            case "getInstances":
                return method.getDeclaringClass() != Event.class ? this : NestedEventConventionContext.INSTANCE;
            case "getInstance":
                return method.getDeclaringClass() != AccessReviewInstanceDecisionItem.class ? this : NestedAccessReviewInstanceConventionContext.INSTANCE;
            case "getReplies":
                return method.getDeclaringClass() != ChatMessage.class ? this : NestedChatMessageConventionContext.INSTANCE;
            case "getValue":
                return method.getDeclaringClass() != StringKeyAttributeMappingSourceValuePair.class ? this : null;
            case "getChildFolders":
                if (method.getDeclaringClass() == ContactFolder.class) {
                    return NestedContactFolderConventionContext.INSTANCE;
                }
                if (method.getDeclaringClass() == MailFolder.class) {
                    return NestedMailFolderConventionContext.INSTANCE;
                }
                return this;
            case "getSessions":
                return method.getDeclaringClass() != VirtualEventPresenter.class ? this : NestedVirtualEventSessionConventionContext.INSTANCE;
            case "getPrinter":
                return method.getDeclaringClass() != PrinterShare.class ? this : NestedPrinterConventionContext.INSTANCE;
            case "getTasks":
                return method.getDeclaringClass() != PrintTaskDefinition.class ? this : NestedPrintTaskConventionContext.INSTANCE;
            case "getParent":
                if (method.getDeclaringClass() == ParentLabelDetails.class) {
                    return NestedParentLabelDetailsConventionContext.INSTANCE;
                }
                if (method.getDeclaringClass() == com.microsoft.graph.beta.models.security.SensitivityLabel.class) {
                    return NestedSecuritySensitivityLabelConventionContext.INSTANCE;
                }
                return this;
            case "getParentNotebook":
                return method.getDeclaringClass() != SectionGroup.class ? this : NestedNotebookConventionContext.INSTANCE;
            case "getParentSectionGroup":
                return method.getDeclaringClass() != SectionGroup.class ? this : NestedSectionGroupConventionContext.INSTANCE;
            case "getParentSection":
                return method.getDeclaringClass() != OnenotePage.class ? this : NestedOnenoteSectionConventionContext.INSTANCE;
            case "getChildren":
                if (method.getDeclaringClass() == DriveItem.class) {
                    return NestedDriveItemConventionContext.INSTANCE;
                }
                if (method.getDeclaringClass() == Term.class || method.getDeclaringClass() == Set.class) {
                    return NestedTermConventionContext.INSTANCE;
                }
                return this;
            case "getSublabels":
                return method.getDeclaringClass() != SensitivityLabel.class ? this : NestedSensitivityLabelConventionContext.INSTANCE;
            case "getTeamsPublicationInfo":
                return concreteClass != PlannerTeamsPublicationInfo.class ? this : NestedPlannerTeamsPublicationInfoConventionContext.INSTANCE;
            case "getManagedDevices":
                return concreteClass != DetectedApp.class ? this : NestedManagedDeviceConventionContext.INSTANCE;
            case "getGroup":
                return method.getDeclaringClass() != Team.class ? this : NestedGroupConventionContext.INSTANCE;
            case "getTeam":
                return method.getDeclaringClass() != TeamInfo.class ? this : NestedTeamConventionContext.INSTANCE;
            case "getInReplyTo":
                return method.getDeclaringClass() != Post.class ? this : NestedPostConventionContext.INSTANCE;
            case "getSites":
                return method.getDeclaringClass() != Site.class ? this : NestedSiteConventionContext.INSTANCE;
            case "getSet":
                return method.getDeclaringClass() != Relation.class ? this : NestedSetConventionContext.INSTANCE;
            case "getSets":
                return method.getDeclaringClass() != Group.class ? this : NestedSetConventionContext.INSTANCE;
            case "getParentGroup":
                return method.getDeclaringClass() != Group.class ? this : NestedGroupConventionContext.INSTANCE;
            case "getDrive":
                return method.getDeclaringClass() != List.class ? this : NestedDriveConventionContext.INSTANCE;
            case "getDriveItem":
                return method.getDeclaringClass() != ItemActivity.class
                        && method.getDeclaringClass() != ItemActivityOLD.class
                        && method.getDeclaringClass() != ListItem.class ? this : NestedDriveItemConventionContext.INSTANCE;
            case "getActivities":
                return method.getDeclaringClass() != ListItem.class ? this : NestedItemActivityConventionContext.INSTANCE;
            case "getWorksheet":
                return method.getDeclaringClass() != WorkbookChart.class
                        && method.getDeclaringClass() != WorkbookNamedItem.class
                        && method.getDeclaringClass() != WorkbookTable.class
                        && method.getDeclaringClass() != WorkbookPivotTable.class ? this : NestedWorksheetConventionContext.INSTANCE;
            case "getSourceColumn":
                return method.getDeclaringClass() != ColumnDefinition.class ? this : null;
            case "getInnerError":
                return method.getDeclaringClass() != WorkbookOperationError.class ? this : null;
            case "getBase":
            case "getBaseTypes":
                return method.getDeclaringClass() != ContentType.class ? this : NestedContentTypeConventionContext.INSTANCE;
            case "getTerms":
                return method.getDeclaringClass() != Set.class ? this : NestedTermConventionContext.INSTANCE;
            case "getFromTerm":
            case "getToTerm":
                return method.getDeclaringClass() != Relation.class ? this : NestedTermConventionContext.INSTANCE;
            case "getCreatedByUser":
            case "getLastModifiedByUser":
                return method.getDeclaringClass() != BaseItem.class ? this : NestedUserConventionContext.INSTANCE;
            default:
                return this;
        }
    }

    private static final class NestedUserConventionContext implements ConventionContext {

        private static final NestedUserConventionContext INSTANCE = new NestedUserConventionContext();

        @Override
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
        public ConventionContext getSubFilter(Class<?> concreteClass, Method method) {
            switch ( method.getName() ) {
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
