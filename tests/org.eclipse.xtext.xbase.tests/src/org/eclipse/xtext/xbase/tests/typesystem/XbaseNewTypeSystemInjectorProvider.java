/*
* generated by Xtext
*/
package org.eclipse.xtext.xbase.tests.typesystem;

import static org.eclipse.xtext.util.Strings.*;

import java.lang.reflect.AnnotatedElement;
import java.util.Iterator;
import java.util.List;

import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.jdt.annotation.NonNullByDefault;
import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.xtext.common.types.JvmAnnotationTarget;
import org.eclipse.xtext.common.types.JvmFormalParameter;
import org.eclipse.xtext.common.types.JvmOperation;
import org.eclipse.xtext.common.types.JvmParameterizedTypeReference;
import org.eclipse.xtext.common.types.JvmSynonymTypeReference;
import org.eclipse.xtext.common.types.JvmType;
import org.eclipse.xtext.common.types.JvmTypeReference;
import org.eclipse.xtext.common.types.access.ClasspathTypeProviderFactory;
import org.eclipse.xtext.common.types.access.impl.ClasspathTypeProvider;
import org.eclipse.xtext.common.types.access.impl.DeclaredTypeFactory;
import org.eclipse.xtext.common.types.util.ITypeArgumentContext;
import org.eclipse.xtext.common.types.util.TypeArgumentContextProvider;
import org.eclipse.xtext.generator.trace.ILocationData;
import org.eclipse.xtext.linking.LinkingScopeProviderBinding;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.scoping.IScope;
import org.eclipse.xtext.scoping.IScopeProvider;
import org.eclipse.xtext.service.SingletonBinding;
import org.eclipse.xtext.validation.Check;
import org.eclipse.xtext.xbase.XAbstractFeatureCall;
import org.eclipse.xtext.xbase.XExpression;
import org.eclipse.xtext.xbase.XbaseStandaloneSetup;
import org.eclipse.xtext.xbase.compiler.Later;
import org.eclipse.xtext.xbase.compiler.XbaseCompiler;
import org.eclipse.xtext.xbase.compiler.output.ITreeAppendable;
import org.eclipse.xtext.xbase.interpreter.IExpressionInterpreter;
import org.eclipse.xtext.xbase.interpreter.impl.XbaseInterpreter;
import org.eclipse.xtext.xbase.resource.BatchLinkableResource;
import org.eclipse.xtext.xbase.scoping.batch.IFeatureNames;
import org.eclipse.xtext.xbase.scoping.batch.XbaseBatchScopeProvider;
import org.eclipse.xtext.xbase.tests.XbaseInjectorProvider;
import org.eclipse.xtext.xbase.typesystem.IBatchTypeResolver;
import org.eclipse.xtext.xbase.typesystem.IResolvedTypes;
import org.eclipse.xtext.xbase.typesystem.legacy.XbaseBatchTypeProvider;
import org.eclipse.xtext.xbase.typesystem.references.FunctionTypeReference;
import org.eclipse.xtext.xbase.typesystem.references.ITypeReferenceOwner;
import org.eclipse.xtext.xbase.typesystem.references.LightweightBoundTypeArgument;
import org.eclipse.xtext.xbase.typesystem.references.LightweightTypeReference;
import org.eclipse.xtext.xbase.typesystem.references.OwnedConverter;
import org.eclipse.xtext.xbase.typesystem.references.WildcardTypeReference;
import org.eclipse.xtext.xbase.typesystem.util.CommonTypeComputationServices;
import org.eclipse.xtext.xbase.typing.XbaseTypeProvider;
import org.eclipse.xtext.xbase.validation.IssueCodes;
import org.eclipse.xtext.xbase.validation.XbaseJavaValidator;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.inject.Binder;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;

/**
 * An injector provider for plain Xbase tests with the reworked type system infrastructure.
 * 
 * @author Sebastian Zarnekow - Initial contribution and API
 */
public class XbaseNewTypeSystemInjectorProvider extends XbaseInjectorProvider {

	@Override
	protected Injector internalCreateInjector() {
		return new XbaseNewTypeSystemTestStandaloneSetup().createInjectorAndDoEMFRegistration();
	}

	public static class XbaseNewTypeSystemTestStandaloneSetup extends XbaseStandaloneSetup {
		@Override
		public Injector createInjector() {
			return Guice.createInjector(new XbaseNewTypeSystemTestRuntimeModule());
		}
	}

	public static class XbaseNewTypeSystemTestRuntimeModule extends XbaseTestRuntimeModule {

		@Override
		public Class<? extends IScopeProvider> bindIScopeProvider() {
			return DisabledXbaseScopeProvider.class;
		}

		@Override
		public void configureLinkingIScopeProvider(Binder binder) {
			binder.bind(IScopeProvider.class).annotatedWith(LinkingScopeProviderBinding.class)
					.to(XbaseBatchScopeProvider.class);
		}

		public Class<? extends ClasspathTypeProviderFactory> bindClasspathTypeProviderFactory() {
			return ClasspathTypeProviderFactoryWithoutAnnotationValues.class;
		}

		@Override
		public Class<? extends XtextResource> bindXtextResource() {
			return BatchLinkableResource.class;
		}

		public Class<? extends XbaseTypeProvider> bindXbaseTypeProvider() {
			return XbaseBatchTypeProvider.class;
		}
		
		@Override
		public Class<? extends IExpressionInterpreter> bindIExpressionInterpreter() {
			return XbaseInterpreter2.class;
		}
		
		public Class<? extends XbaseCompiler> bindCompiler() {
			return XbaseCompiler2.class;
		}
		
		@Override
		@SingletonBinding(eager=true)	
		public Class<? extends XbaseJavaValidator> bindXbaseJavaValidator() {
			return XbaseJavaValidator2.class;
		}
		
	}
	
	public static class XbaseJavaValidator2 extends XbaseJavaValidator {
		@Override
		@Check
		public void checkImplicitReturn(XExpression expr) {
			// temporarily disabled
		}
		
		@Override
		protected void checkDeclaredVariableName(EObject nameDeclarator, EObject attributeHolder, EAttribute attr) {
			if (nameDeclarator.eContainer() == null)
				return;
			if (attr.getEContainingClass().isInstance(attributeHolder)) {
				String name = (String) attributeHolder.eGet(attr);
				// shadowing 'it' is allowed
				if(name == null || equal(name, IFeatureNames.IT.toString()))
					return;
				if (getDisallowedVariableNames().contains(name)) {
					error("'" + name + "' is not a valid name.", attributeHolder, attr, -1,
							IssueCodes.VARIABLE_NAME_SHADOWING);
					return;
				}
				// temporarily disabled
//				int idx = 0;
//				if (nameDeclarator.eContainer() instanceof XBlockExpression) {
//					idx = ((XBlockExpression) nameDeclarator.eContainer()).getExpressions().indexOf(nameDeclarator);
//				}
//				IScope scope = getScopeProvider().createSimpleFeatureCallScope(nameDeclarator.eContainer(),
//						XbasePackage.Literals.XABSTRACT_FEATURE_CALL__FEATURE, nameDeclarator.eResource(), true, idx);
//				Iterable<IEObjectDescription> elements = scope.getElements(QualifiedName.create(name));
//				for (IEObjectDescription desc : elements) {
//					if (desc.getEObjectOrProxy() != nameDeclarator && !(desc.getEObjectOrProxy() instanceof JvmFeature)) {
//						error("Duplicate variable name '" + name + "'", attributeHolder, attr,
//								IssueCodes.VARIABLE_NAME_SHADOWING);
//					}
//				}
			}
		}
		
		@SuppressWarnings("deprecation")
		@Override
		protected org.eclipse.xtext.xbase.scoping.XbaseScopeProvider getScopeProvider() {
			throw new UnsupportedOperationException();
		}
	}

	public static class XbaseInterpreter2 extends XbaseInterpreter {
		@Override
		protected List<XExpression> getActualArguments(XAbstractFeatureCall featureCall) {
			return featureCall.getActualArguments();
		}
		@Override
		protected XExpression getActualReceiver(XAbstractFeatureCall featureCall) {
			return featureCall.getActualReceiver();
		}
	}
	
	@NonNullByDefault
	public static class TypeReferenceOwner implements ITypeReferenceOwner {

		private CommonTypeComputationServices services;
		private ResourceSet context;

		public TypeReferenceOwner(CommonTypeComputationServices services, ResourceSet context) {
			this.services = services;
			this.context = context;
		}
		
		public CommonTypeComputationServices getServices() {
			return services;
		}

		public ResourceSet getContextResourceSet() {
			return context;
		}

		public void acceptHint(Object handle, LightweightBoundTypeArgument boundTypeArgument) {
			throw new UnsupportedOperationException();
		}

		public List<LightweightBoundTypeArgument> getAllHints(Object handle) {
			throw new UnsupportedOperationException();
		}

		public boolean isResolved(Object handle) {
			throw new UnsupportedOperationException();
		}
		
	}
	
	@NonNullByDefault
	public static class XbaseCompiler2 extends XbaseCompiler {
		
		@Inject 
		private IBatchTypeResolver batchTypeResolver;
		
		@Inject
		private CommonTypeComputationServices services;
		
		@Override
		protected List<XExpression> getActualArguments(XAbstractFeatureCall featureCall) {
			return featureCall.getActualArguments();
		}
		@Nullable
		@Override
		protected XExpression getActualReceiver(XAbstractFeatureCall featureCall) {
			return featureCall.getActualReceiver();
		}
		@Override
		protected boolean isMemberCall(XAbstractFeatureCall call) {
			return !call.isStatic();
		}
		
		@Override
		protected ITreeAppendable appendTypeArguments(XAbstractFeatureCall call, ITreeAppendable original) {
			if (!call.getTypeArguments().isEmpty()) {
				return super.appendTypeArguments(call, original);
			}
			ILocationData completeLocationData = getLocationWithTypeArguments(call);
			ITreeAppendable completeFeatureCallAppendable = completeLocationData != null ? original.trace(completeLocationData) : original;
			IResolvedTypes resolvedTypes = batchTypeResolver.resolveTypes(call);
			List<LightweightTypeReference> typeArguments = resolvedTypes.getActualTypeArguments(call);
			if (!typeArguments.isEmpty()) {
				List<JvmTypeReference> resolvedTypeArguments = Lists.newArrayListWithCapacity(typeArguments.size());
				for(LightweightTypeReference typeArgument: typeArguments) {
					if (typeArgument.isWildcard()) {
						return completeFeatureCallAppendable;
					}
					JvmTypeReference jvmTypeReference = resolveMultiType(typeArgument.toTypeReference());
					resolvedTypeArguments.add(jvmTypeReference);
				}
				completeFeatureCallAppendable.append("<");
				for (int i = 0; i < resolvedTypeArguments.size(); i++) {
					if (i != 0) {
						completeFeatureCallAppendable.append(", ");
					}
					JvmTypeReference typeArgument = resolvedTypeArguments.get(i);
					serialize(typeArgument, call, completeFeatureCallAppendable);
				}
				completeFeatureCallAppendable.append(">");
			}
			return completeFeatureCallAppendable;
		}
		
		/*
		 * Pretty much a copy of the super impl but without the type argument context geraffel.
		 */
		@Override
		protected void convertFunctionType(JvmTypeReference expectedType, final JvmTypeReference functionType,
				ITreeAppendable appendable, Later expression, XExpression context) {
			if (expectedType.getIdentifier().equals(Object.class.getName())
					|| EcoreUtil.equals(expectedType.getType(), functionType.getType())
					|| ((expectedType instanceof JvmSynonymTypeReference) 
						&& Iterables.any(((JvmSynonymTypeReference)expectedType).getReferences(), new Predicate<JvmTypeReference>() {
							public boolean apply(@Nullable JvmTypeReference ref) {
								return EcoreUtil.equals(ref.getType(), functionType.getType());
							}
						}))) {
				// same raw type but different type parameters
				// at this point we know that we are compatible so we have to convince the Java compiler about that ;-)
				if (!getTypeConformanceComputer().isConformant(expectedType, functionType)) {
					// insert a cast
					appendable.append("(");
					serialize(expectedType, context, appendable);
					appendable.append(")");
				}
				expression.exec(appendable);
				return;
			}
			JvmOperation operation = getClosures().findImplementingOperation(expectedType, context.eResource());
			if (operation == null) {
				throw new IllegalStateException("expected type " + expectedType + " not mappable from " + functionType);
			}
			appendable.append("new ");
			TypeReferenceOwner owner = new TypeReferenceOwner(services, context.eResource().getResourceSet());
			LightweightTypeReference lightweightExpectedType = new OwnedConverter(owner).toLightweightReference(expectedType);
			FunctionTypeReference functionTypeReference = lightweightExpectedType.tryConvertToFunctionTypeReference(false);
			if (functionTypeReference == null)
				throw new IllegalStateException("Expected type does not seem to be a SAM type");
			JvmTypeReference convertedExpectedType = functionTypeReference.toInstanceTypeReference().toTypeReference();
			serialize(convertedExpectedType, context, appendable, false, false);
			appendable.append("() {");
			appendable.increaseIndentation().increaseIndentation();
			appendable.newLine().append("public ");
			LightweightTypeReference returnType = functionTypeReference.getReturnType();
			if (returnType == null)
				throw new IllegalStateException("Could not find return type");
			serialize(returnType.toTypeReference(), context, appendable, false, false);
			appendable.append(" ").append(operation.getSimpleName()).append("(");
			List<JvmFormalParameter> params = operation.getParameters();
			for (int i = 0; i < params.size(); i++) {
				if (i != 0)
					appendable.append(",");
				JvmFormalParameter p = params.get(i);
				final String name = p.getName();
				serialize(functionTypeReference.getParameterTypes().get(i).toTypeReference(), context, appendable, false, false);
				appendable.append(" ").append(name);
			}
			appendable.append(") {");
			appendable.increaseIndentation();
			if (!getTypeReferences().is(operation.getReturnType(), Void.TYPE))
				appendable.newLine().append("return ");
			else
				appendable.newLine();
			expression.exec(appendable);
			appendable.append(".");
			JvmOperation actualOperation = getClosures().findImplementingOperation(functionType, context.eResource());
			appendable.append(actualOperation.getSimpleName());
			appendable.append("(");
			for (Iterator<JvmFormalParameter> iterator = params.iterator(); iterator.hasNext();) {
				JvmFormalParameter p = iterator.next();
				final String name = p.getName();
				appendable.append(name);
				if (iterator.hasNext())
					appendable.append(",");
			}
			appendable.append(");");
			appendable.decreaseIndentation();
			appendable.newLine().append("}");
			appendable.decreaseIndentation().decreaseIndentation();
			appendable.newLine().append("}");
		}
		
	}
	
	@SuppressWarnings("deprecation")
	public static class DisabledXbaseScopeProvider extends org.eclipse.xtext.xbase.scoping.XbaseScopeProvider {
		@Override
		public IScope getScope(EObject context, EReference reference) {
			throw new UnsupportedOperationException();
		}
	}

	public static class ClasspathTypeProviderFactoryWithoutAnnotationValues extends ClasspathTypeProviderFactory {

		@Inject
		public ClasspathTypeProviderFactoryWithoutAnnotationValues(ClassLoader classLoader) {
			super(classLoader);
		}

		@Override
		protected ClasspathTypeProvider createClasspathTypeProvider(ResourceSet resourceSet) {
			return new ClasspathTypeProvider(getClassLoader(), resourceSet, getIndexedJvmTypeAccess()) {
				@Override
				protected DeclaredTypeFactory createDeclaredTypeFactory() {
					return new DeclaredTypeFactory(getClassURIHelper()) {
						@Override
						protected void createAnnotationValues(AnnotatedElement annotated, JvmAnnotationTarget result) {
							// disabled for performance reasons
						}
					};
				}
			};
		}
	}

}
