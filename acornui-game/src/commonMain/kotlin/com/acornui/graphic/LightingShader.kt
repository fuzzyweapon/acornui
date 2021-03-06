package com.acornui.graphic

import com.acornui.gl.core.*

const val PACK_FLOAT: String = """
vec4 packFloat(const in float value) {
	const vec4 bit_shift = vec4(256.0 * 256.0 * 256.0, 256.0 * 256.0, 256.0, 1.0);
	const vec4 bit_mask  = vec4(0.0, 1.0 / 256.0, 1.0 / 256.0, 1.0 / 256.0);
	vec4 res = fract(value * bit_shift);
	res -= res.xxyz * bit_mask;
	return res;
}
"""

const val UNPACK_FLOAT: String = """
float unpackFloat(const in vec4 rgba_depth) {
	const vec4 bit_shift = vec4(1.0/(256.0*256.0*256.0), 1.0/(256.0*256.0), 1.0/256.0, 1.0);
	float depth = dot(rgba_depth, bit_shift);
	return depth;
}
"""

class LightingShader(gl: Gl20, numPointLights: Int, numShadowPointLights: Int) : ShaderProgramBase(
		gl, vertexShaderSrc = """
$DEFAULT_SHADER_HEADER

attribute vec3 a_position;
attribute vec3 a_normal;
attribute vec4 a_colorTint;
attribute vec2 a_texCoord0;

uniform mat4 u_projTrans;
uniform mat4 u_modelTrans;
uniform mat4 u_directionalLightMvp;

varying vec4 v_worldPosition;
varying vec3 v_normal;
varying vec4 v_colorTint;
varying vec2 v_texCoord;
varying vec4 v_directionalShadowCoord;

void main() {
	v_worldPosition = u_modelTrans * vec4(a_position, 1.0);
	v_normal = normalize(mat3(u_modelTrans) * a_normal);
	v_colorTint = a_colorTint;
	v_texCoord = a_texCoord0;
	gl_Position =  u_projTrans * v_worldPosition;
	v_directionalShadowCoord = u_directionalLightMvp * v_worldPosition;
}
""", fragmentShaderSrc = """
$DEFAULT_SHADER_HEADER

struct PointLight {
	float radius;
	vec3 position;
	vec3 color;
};

varying vec4 v_worldPosition;
varying vec3 v_normal;
varying vec4 v_colorTint;
varying vec4 v_directionalShadowCoord;
varying vec2 v_texCoord;

uniform int u_shadowsEnabled;
uniform vec2 u_resolutionInv;
uniform vec4 u_ambient;
uniform vec4 u_directional;
uniform vec3 u_directionalLightDir;
uniform sampler2D u_texture;
uniform sampler2D u_directionalShadowMap;

uniform bool u_useColorTrans;
uniform mat4 u_colorTrans;
uniform vec4 u_colorOffset;


// Point lights
${if (numPointLights > 0) "uniform PointLight u_pointLights[$numPointLights];" else ""}
${if (numShadowPointLights > 0) "uniform samplerCube u_pointLightShadowMaps[$numShadowPointLights];" else ""}

uniform vec2 poissonDisk[4];

$UNPACK_FLOAT

float getShadowDepth(const in vec2 coord) {
	vec4 c = texture2D(u_directionalShadowMap, coord);
	return unpackFloat(c);
}

vec3 getDirectionalColor() {
	float cosTheta = clamp(dot(v_normal, -u_directionalLightDir), 0.05, 1.0);
	if (u_shadowsEnabled == 0 || u_directional.rgb == vec3(0.0)) return cosTheta * u_directional.rgb;
	float visibility = 0.0;
	float shadow = getShadowDepth(v_directionalShadowCoord.xy);
	float bias = 0.002;
	float testZ = v_directionalShadowCoord.z - bias;
	if (testZ >= unpackFloat(vec4(0.0, 0.0, 1.0, 1.0)))
    	return cosTheta * u_directional.rgb;

	if (shadow >= testZ) visibility += 0.2;
	for (int i = 0; i < 4; i++) {
		shadow = getShadowDepth((v_directionalShadowCoord.xy + poissonDisk[i] * u_resolutionInv));
		if (shadow >= testZ) visibility += 0.2;
	}

	return visibility * cosTheta * u_directional.rgb;
}

vec3 getPointColor() {
${if (numPointLights > 0) """
	vec3 pointColor = vec3(0.0);
	PointLight pointLight;
	vec3 lightToPixel;
	vec3 lightToPixelN;
	float attenuation;
	float distance;
	float shadow;
	float testZ;

	float bias = -0.0005;
	float maxD = unpackFloat(vec4(0.0, 0.0, 1.0, 1.0));

	for (int i = 0; i < $numPointLights; i++) {
		pointLight = u_pointLights[i];
		if (pointLight.radius > 1.0) {
			lightToPixel = v_worldPosition.xyz - pointLight.position;
			lightToPixelN = normalize(lightToPixel);
			distance = length(lightToPixel) / pointLight.radius;
			testZ = distance - bias;

			attenuation = 1.0 - clamp(distance, 0.0, 1.0);
			float cosTheta = clamp(dot(v_normal, -1.0 * lightToPixelN), 0.0, 1.0);

			if (u_shadowsEnabled == 0 || i >= $numShadowPointLights) {
				pointColor += cosTheta * pointLight.color * attenuation * attenuation;
			} else {
				${if (numShadowPointLights > 0) """
				shadow = unpackFloat(textureCube(u_pointLightShadowMaps[i], lightToPixelN));

				if (testZ >= maxD || shadow >= testZ) {
					pointColor += cosTheta * pointLight.color * attenuation * attenuation;
				}
				""" else ""}
			}
		}
	}

	return pointColor;
	""" else "return vec3(0.0);"}
}

void main() {
	vec3 directional = getDirectionalColor();
	vec3 point = getPointColor();

	vec4 diffuseColor = v_colorTint * texture2D(u_texture, v_texCoord);

	vec4 final = vec4(clamp(u_ambient.rgb + directional + point, 0.0, 1.3) * diffuseColor.rgb, diffuseColor.a);
	if (u_useColorTrans) {
		gl_FragColor = u_colorTrans * final + u_colorOffset;
	} else {
		gl_FragColor = final;
	}

	if (gl_FragColor.a < 0.01) discard;
}


"""
) {

	private var isFirst = true

	override fun bind() {
		super.bind()

		if (isFirst) {
			isFirst = false
			// Poisson disk
			gl.uniform2f(getRequiredUniformLocation("poissonDisk[0]"), -0.94201624f, -0.39906216f)
			gl.uniform2f(getRequiredUniformLocation("poissonDisk[1]"), 0.94558609f, -0.76890725f)
			gl.uniform2f(getRequiredUniformLocation("poissonDisk[2]"), -0.09418410f, -0.92938870f)
			gl.uniform2f(getRequiredUniformLocation("poissonDisk[3]"), 0.34495938f, 0.29387760f)
		}
	}
}

class PointShadowShader(gl: Gl20) : ShaderProgramBase(
		gl, vertexShaderSrc = """
$DEFAULT_SHADER_HEADER

attribute vec3 a_position;
attribute vec4 a_colorTint;
attribute vec2 a_texCoord0;

uniform mat4 u_pointLightMvp;
uniform mat4 u_modelTrans;

varying vec4 v_worldPosition;
varying vec4 v_colorTint;
varying vec2 v_texCoord;

void main() {
	v_colorTint = a_colorTint;
	v_texCoord = a_texCoord0;
	v_worldPosition = u_modelTrans * vec4(a_position, 1.0);
	gl_Position = u_pointLightMvp * v_worldPosition;
}
""", fragmentShaderSrc = """
$DEFAULT_SHADER_HEADER

uniform vec3 u_lightPosition;
uniform float u_lightRadius;
varying vec4 v_colorTint;
varying vec2 v_texCoord;

uniform sampler2D u_texture;

varying vec4 v_worldPosition;

$PACK_FLOAT

void main() {
	vec4 diffuse = v_colorTint * texture2D(u_texture, v_texCoord);
	if (diffuse.a < 0.2) discard;
	gl_FragColor = packFloat(length(v_worldPosition.xyz - u_lightPosition) / u_lightRadius);
}

"""
)

class DirectionalShadowShader(gl: Gl20) : ShaderProgramBase(
		gl, vertexShaderSrc = """
$DEFAULT_SHADER_HEADER

attribute vec3 a_position;
attribute vec4 a_colorTint;
attribute vec2 a_texCoord0;

uniform mat4 u_directionalLightMvp;
uniform mat4 u_modelTrans;

varying vec4 v_colorTint;
varying vec2 v_texCoord;

void main() {
	v_colorTint = a_colorTint;
	v_texCoord = a_texCoord0;
	vec4 worldPosition = u_modelTrans * vec4(a_position, 1.0);
	gl_Position = u_directionalLightMvp * worldPosition;
}
""", fragmentShaderSrc = """
$DEFAULT_SHADER_HEADER

varying vec4 v_colorTint;
varying vec2 v_texCoord;

uniform sampler2D u_texture;

$PACK_FLOAT

void main() {
	vec4 diffuse = v_colorTint * texture2D(u_texture, v_texCoord);
	if (diffuse.a < 0.2) discard;
	gl_FragColor = packFloat(gl_FragCoord.z);
}

"""
) {

	override fun bind() {
		super.bind()
		gl.uniform1i(getUniformLocation(CommonShaderUniforms.U_TEXTURE)!!, 0);  // set the fragment shader's texture to unit 0
	}

}