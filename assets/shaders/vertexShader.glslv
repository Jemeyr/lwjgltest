#version 150

uniform mat4 viewMatrix, projMatrix;
uniform vec3 cam;

in vec3 position;
in vec3 normal;
in vec2 texCoord;

out vec2 texCoordPS;
smooth out float light;

void main() {
	vec4 pos = vec4(position.xyz, 1.0);

	light = 0.1 + abs(dot(normal, normalize(cam)));

	texCoordPS = texCoord;
	gl_Position =   projMatrix * viewMatrix * pos;

}