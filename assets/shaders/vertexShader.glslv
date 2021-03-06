#version 150

uniform mat4 viewMatrix, projMatrix;

in vec3 position;
in vec3 normal;
in vec2 texCoord;

smooth out vec3 norm;
out vec2 texCoordPS;

void main() {

	vec4 pos = vec4(position.xyz, 1.0);
	norm = normal;

	texCoordPS = texCoord;
	gl_Position =   projMatrix * viewMatrix * pos;
}